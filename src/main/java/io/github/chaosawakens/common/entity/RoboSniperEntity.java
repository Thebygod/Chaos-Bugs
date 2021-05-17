package io.github.chaosawakens.common.entity;

import io.github.chaosawakens.common.entity.ai.RoboAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class RoboSniperEntity extends RoboEntity implements IAnimatable, IRangedAttackMob {
    private final AnimationFactory factory = new AnimationFactory(this);

    public RoboSniperEntity(EntityType<? extends RoboEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreFrustumCheck = true;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(event.isMoving() && !this.isAggressive()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.robo_sniper.walking_animation", true));
            return PlayState.CONTINUE;
        }
        if(event.isMoving() && this.isAggressive()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.robo_sniper.accelerate_animation", true));
            return PlayState.CONTINUE;
        }
        if (!event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.robo_sniper.idle_animation", true));
            return PlayState.CONTINUE;
        }
        if(this.didLaser) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.robo_sniper.attacking_animation", true));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 24.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, IronGolemEntity.class, 24.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, SnowGolemEntity.class, 24.0F));
        this.goalSelector.addGoal(3, new RoboAttackGoal(this));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.6));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(7, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SnowGolemEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.registerAttributes()
                .createMutableAttribute(Attributes.MAX_HEALTH, 25)
                .createMutableAttribute(Attributes.ARMOR, 3)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1)
                .createMutableAttribute(Attributes.ATTACK_SPEED, 10)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 25)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 3.5D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "robosnipercontroller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        this.getAttackTarget();
    }
}