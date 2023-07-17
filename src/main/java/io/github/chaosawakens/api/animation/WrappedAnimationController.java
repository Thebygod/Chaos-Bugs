package io.github.chaosawakens.api.animation;

import java.util.ArrayList;

import io.github.chaosawakens.common.network.packets.s2c.AnimationFunctionalProgressPacket;
import io.github.chaosawakens.manager.CANetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;

public class WrappedAnimationController<E extends IAnimatableEntity> {
	protected E animatable;
	protected String name;
	protected ExpandedAnimationState animationState = ExpandedAnimationState.FINISHED;	
	protected Animation currentAnimation = none();
	protected double transitionLength;
	protected double transitionProgress = 0;
	protected double animationLength;
	protected double animationProgress = 0;	
	protected final AnimationController<E> controller;
	protected MinecraftServer server;
	
	public WrappedAnimationController(E animatable, int transitionTicks, AnimationController<E> controller) {
		this.animatable = animatable;
		this.transitionLength = transitionTicks;
		this.controller = controller;
		this.name = controller.getName();
		this.server = ((Entity) animatable).getServer();
	}
	
	public WrappedAnimationController(E animatable, AnimationController<E> controller) {
		this.animatable = animatable;
		this.transitionLength = animatable.animationInterval();
		this.controller = controller;
		this.name = controller.getName();
		this.server = ((Entity) animatable).getServer();
	}
	
	public void tick() {
		double tickProgressDelta = server == null ? 0 : getSyncedProgress(Math.max(server.getNextTickTime() - Util.getMillis(), 0.0) / 50.0);
		
		switch (animationState) {
		case TRANSITIONING:
			if (this.transitionProgress >= this.transitionLength) {
				this.transitionProgress = 0;
				this.animationState = ExpandedAnimationState.RUNNING;
			} else {
				this.transitionProgress += tickProgressDelta;
			}
			break;
		case RUNNING:
			if (this.animationProgress >= this.animationLength) {
				this.animationProgress = 0;
				if (this.currentAnimation.loop == EDefaultLoopTypes.LOOP) {
					this.animationProgress = 0;
					this.animationState = ExpandedAnimationState.TRANSITIONING;
				} else {
					this.animationState = ExpandedAnimationState.FINISHED;
				}
			} else {
				this.animationProgress += tickProgressDelta;
			}
			break;
		case STOPPED:
			break;
		case FINISHED:
			break;
		}
	}
	
	public void playAnimation(IAnimationBuilder builder, boolean clearCache) {
		if (builder == null) {
			this.animationProgress = 0;
			this.animationLength = 0;
			this.transitionProgress = 0;
			this.animationState = ExpandedAnimationState.FINISHED;
		}
		
		if (!getCurrentAnimation().animationName.equals(builder.getAnimationName()) || clearCache) {
			if (clearCache) builder.playAnimation(true);
			else builder.playAnimation(false);
			
			this.animationProgress = 0;
			this.animationLength = builder.getAnimation().animationLength;
			this.transitionProgress = 0;
			this.animationState = ExpandedAnimationState.TRANSITIONING;
		}
		this.currentAnimation = builder.getAnimation();
		this.controller.setAnimation(builder.getBuilder());
	}
	
	public double getSyncedProgress(double animProgress) {
		if (server != null) CANetworkManager.sendEntityTrackingPacket(new AnimationFunctionalProgressPacket(name, ((Entity) animatable).getId(), animProgress), (Entity) animatable);
		return server == null ? 0 : Math.max(server.getNextTickTime() - Util.getMillis(), 0.0) / 50.0;
	}
	
	public void updateAnimProgress(double animationProgressDelta) {
		this.animationProgress += animationProgressDelta;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isCurrentAnimationFinished() {
		return animationState.equals(ExpandedAnimationState.FINISHED);
	}
	
	public boolean isAnimationFinished(String targetAnimName) {
		return currentAnimation != null && currentAnimation.animationName.equals(targetAnimName) && animationState.equals(ExpandedAnimationState.FINISHED);
	}
	
	public boolean isAnimationFinished(IAnimationBuilder targetAnim) {
		return isAnimationFinished(targetAnim.getAnimationName());
	}
	
	public boolean isPlayingAnimation(String targetAnimName) {
		return currentAnimation != null && currentAnimation.animationName.equals(targetAnimName) && (animationState.equals(ExpandedAnimationState.RUNNING) || animationState.equals(ExpandedAnimationState.TRANSITIONING));
	}
	
	public boolean isPlayingAnimation(IAnimationBuilder targetAnim) {
		return isPlayingAnimation(targetAnim.getAnimationName());
	}
	
	public ExpandedAnimationState getAnimationState() {
		return animationState;
	}

	public double getAnimationProgressTicks() {
		return Math.ceil(animationProgress) + 3;
	}
	
	public double getAnimationLength() {
		return Math.floor(animationLength) - 4;
	}
	
	public AnimationController<E> getWrappedController() {
		return controller;
	}
	
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	public static Animation none() {
		Animation noneAnimation = new Animation();
		noneAnimation.animationName = "None";
		noneAnimation.boneAnimations = new ArrayList<BoneAnimation>();
		noneAnimation.animationLength = 0.0;
		return noneAnimation;
	}
}
