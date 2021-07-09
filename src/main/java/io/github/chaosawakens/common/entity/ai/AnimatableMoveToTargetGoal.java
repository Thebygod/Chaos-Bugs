package io.github.chaosawakens.common.entity.ai;

import java.util.EnumSet;

import io.github.chaosawakens.common.entity.AnimatableMonsterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;

/**
 * Move a given entity towards another targeted entity
 * @author invalid2
 */
public class AnimatableMoveToTargetGoal extends AnimatableMovableGoal {
	
	private final double speedMultiplier;
	private final int checkRate;
	
	/**
	 * Move an AnimatableMonsterEntity to a target entity
	 * @param entity AnimatableMonsterEntity instance
	 * @param speedMultiplier Entity will move by base speed * this
	 * @param checkRate Check rate with formula: {@code if(RANDOM.nextInt(rate) == 0)}, so bigger = less often
	 */
	public AnimatableMoveToTargetGoal(AnimatableMonsterEntity entity, double speedMultiplier, int checkRate) {
		this.entity = entity;
		this.speedMultiplier = speedMultiplier;
		this.checkRate = checkRate;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean shouldExecute() {
		if(RANDOM.nextInt(this.checkRate) == 0)return false;
		
		return this.isExecutable(this, this.entity, this.entity.getAttackTarget());
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		if(RANDOM.nextInt(this.checkRate) == 0)return true;
		
		return this.isExecutable(this, this.entity, this.entity.getAttackTarget());
	}
	
	@Override
	public void startExecuting() {
		this.entity.setAggroed(true);
		this.entity.setMoving(true);
		this.entity.getNavigator().setPath(this.path, this.speedMultiplier);
	}
	
	@Override
	public void resetTask() {
		LivingEntity target = this.entity.getAttackTarget();
		if (!EntityPredicates.CAN_AI_TARGET.test(target)) {
			this.entity.setAttackTarget(null);
		}
		this.entity.setAggroed(false);
		this.entity.setMoving(false);
		this.entity.getNavigator().clearPath();
	}
	
	@Override
	public void tick() {
		LivingEntity target = this.entity.getAttackTarget();
		if(target == null)return;
		
		this.entity.getLookController().setLookPositionWithEntity(target, 30F, 30F);
		this.entity.getNavigator().tryMoveToEntityLiving(target, this.speedMultiplier);
	}
}