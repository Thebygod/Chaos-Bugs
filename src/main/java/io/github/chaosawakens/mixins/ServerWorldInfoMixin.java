package io.github.chaosawakens.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;

import net.minecraft.world.storage.ServerWorldInfo;

@Mixin(ServerWorldInfo.class)
public class ServerWorldInfoMixin {
	
	//Disable experimental settings warning screen
	@Inject(method = "Lnet/minecraft/world/storage/ServerWorldInfo;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;", at = @At("HEAD"), cancellable = true)
	private void chaosawakens$worldGenSettingsLifecycle(CallbackInfoReturnable<Lifecycle> callback) {
		if (callback.getReturnValue() != Lifecycle.stable()) callback.setReturnValue(Lifecycle.stable());
	}
}