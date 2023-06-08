package io.github.chaosawakens.common.entity.creature.water;

import java.util.Objects;
import java.util.Optional;

import io.github.chaosawakens.api.animation.IAnimatableEntity;
import io.github.chaosawakens.api.animation.SingletonAnimationBuilder;
import io.github.chaosawakens.common.entity.ai.goals.passive.water.whale.WhaleBreatheGoal;
import io.github.chaosawakens.common.entity.base.AnimatableWaterMobEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class WhaleEntity extends AnimatableWaterMobEntity {
	private final AnimationFactory factory = new AnimationFactory(this);
	private final AnimationController<WhaleEntity> mainController = createMainMappedController("whalemaincontroller");
	private final SingletonAnimationBuilder idleAnim = new SingletonAnimationBuilder(this, "Idle", EDefaultLoopTypes.LOOP);
	private final SingletonAnimationBuilder swimAnim = new SingletonAnimationBuilder(this, "Swim", EDefaultLoopTypes.LOOP);
	
	public WhaleEntity(EntityType<? extends WaterMobEntity> type, World world) {
		super(type, world);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
		return MobEntity.createLivingAttributes()
				.add(Attributes.MAX_HEALTH, 120)
				.add(Attributes.MOVEMENT_SPEED, 0.5D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 50D)
				.add(Attributes.FOLLOW_RANGE, 18);
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	@Override
	public AnimationController<? extends IAnimatableEntity> getMainController() {
		return mainController;
	}

	@Override
	public int animationInterval() {
		return 5;
	}

	@Override
	public <E extends IAnimatableEntity> PlayState mainPredicate(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new RandomSwimmingGoal(this, 1.0D, 3));
		this.goalSelector.addGoal(0, new WhaleBreatheGoal(this));
		this.goalSelector.addGoal(1, new FollowBoatGoal(this));
		this.goalSelector.addGoal(1, new FindWaterGoal(this));
	}
	
	public static boolean checkWhaleSpawnRules(IWorld world, BlockPos pos) {
		if (pos.getY() > 25 && pos.getY() < world.getSeaLevel()) {
			Optional<RegistryKey<Biome>> targetBiome = world.getBiomeName(pos);
			return (Objects.equals(targetBiome, Optional.of(Biomes.OCEAN)) || !Objects.equals(targetBiome, Optional.of(Biomes.DEEP_OCEAN))) && world.getFluidState(pos).is(FluidTags.WATER);
		} else return false;
	}

	@Override
	public SingletonAnimationBuilder getIdleAnim() {
		return idleAnim;
	}

	@Override
	public SingletonAnimationBuilder getSwimAnim() {
		return swimAnim;
	}

	@Override
	public SingletonAnimationBuilder getDeathAnim() {
		return null;
	}
	
	@Override
	public int getMaxAirSupply() {
		return 5000;
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectArrayList<AnimationController<WhaleEntity>> getControllers() {
		return new ObjectArrayList<AnimationController<WhaleEntity>>(1);
	}
}