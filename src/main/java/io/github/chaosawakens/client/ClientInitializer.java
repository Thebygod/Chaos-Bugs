package io.github.chaosawakens.client;

import io.github.chaosawakens.entities.ModEntities;
import io.github.chaosawakens.entities.entities.ent.EntEntityRenderer;
import io.github.chaosawakens.items.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricModelPredicateProviderRegistry.register(ModItems.ULTIMATE_BOW, new Identifier("pull"), (itemStack, clientWorld, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 5.0F;
        });

        FabricModelPredicateProviderRegistry.register(ModItems.ULTIMATE_BOW, new Identifier("pulling"), (itemStack, clientWorld, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });


        EntityRendererRegistry.INSTANCE.register(ModEntities.ENT, (entityRenderDispatcher, context) -> new EntEntityRenderer(entityRenderDispatcher));
    }
}