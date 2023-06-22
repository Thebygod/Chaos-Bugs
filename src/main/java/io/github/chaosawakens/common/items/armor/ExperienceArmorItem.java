package io.github.chaosawakens.common.items.armor;

import java.util.List;
import java.util.function.Supplier;

import io.github.chaosawakens.common.items.base.EnchantedArmorItem;
import io.github.chaosawakens.common.registry.CAItems;
import io.github.chaosawakens.common.util.EntityUtil;
import io.github.chaosawakens.common.util.EnumUtil.CAArmorMaterial;
import io.github.chaosawakens.manager.CAConfigManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;

public class ExperienceArmorItem extends EnchantedArmorItem {

	public ExperienceArmorItem(CAArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn, Supplier<EnchantmentData[]> enchantments) {
		super(materialIn, slot, builderIn, enchantments);
	}
	
	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (EntityUtil.isFullArmorSet(player, CAItems.EXPERIENCE_HELMET.get(), CAItems.EXPERIENCE_CHESTPLATE.get(), CAItems.EXPERIENCE_LEGGINGS.get(), CAItems.EXPERIENCE_BOOTS.get())) {
			PickupXp xpPickupEvent = new PickupXp(player, new ExperienceOrbEntity(world, player.getX(), player.getY(), player.getZ(), player.level.random.nextInt(player.getXpNeededForNextLevel()) / 3));
			MinecraftForge.EVENT_BUS.post(xpPickupEvent);
			xpPickupEvent.setCanceled(false);
			
			if (player.getRandom().nextInt(CAConfigManager.MAIN_COMMON.experienceArmorSetRandomBonusXPSpawnTime.get()) == 0 && !player.level.isClientSide && CAConfigManager.MAIN_COMMON.enableExperienceArmorSetBonus.get()) {
				ExperienceOrbEntity xp = xpPickupEvent.getOrb();
				xp.value = player.level.random.nextInt(player.getXpNeededForNextLevel() - 6 + player.level.random.nextInt(3));
				
				if (player.experienceLevel >= 30) xp.value = player.level.random.nextInt(player.getXpNeededForNextLevel() - 12 + player.level.random.nextInt(17));
				
				player.level.addFreshEntity(xp);
				xpPickupEvent.setCanceled(true);
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (!CAConfigManager.MAIN_CLIENT.enableTooltips.get()) return;
		super.appendHoverText(stack, world, tooltip, flag);
		
		tooltip.add(new StringTextComponent("Full Set Bonus: ").withStyle(TextFormatting.GOLD).append(new StringTextComponent("Experienced").withStyle(TextFormatting.YELLOW)).append(new StringTextComponent(" (...)").withStyle(TextFormatting.GREEN)));
		
		if (Screen.hasShiftDown() || Screen.hasControlDown()) {
			tooltip.removeIf((s) -> s.toString().contains("(...)"));
			tooltip.add(new StringTextComponent("Full Set Bonus: ").withStyle(TextFormatting.GOLD).append(new StringTextComponent("Experienced").withStyle(TextFormatting.YELLOW))
					.append(new StringTextComponent("\nExperience Orbs spawn occasionally on the player. All XP drops (excluding ones from the armor itself) have a " + CAConfigManager.MAIN_COMMON.experienceArmorSetXPMultiplier.get() + "X multiplier.").withStyle(TextFormatting.GREEN)));
		}
		
		if (!CAConfigManager.MAIN_COMMON.enableExperienceArmorSetBonus.get()) {
			tooltip.add(new StringTextComponent("This full set bonus is disabled in the config!").withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD));
		}
	}
}
