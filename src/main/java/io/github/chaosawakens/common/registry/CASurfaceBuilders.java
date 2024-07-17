package io.github.chaosawakens.common.registry;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.common.worldgen.surfacebuilder.StalagmiteValleySurfaceBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;


public class CASurfaceBuilders {
	public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, ChaosAwakens.MODID);
	
	public static final RegistryObject<StalagmiteValleySurfaceBuilder> STALAGMITE_VALLEY = registerSurfaceBuilder("stalagmite_valley", () -> new StalagmiteValleySurfaceBuilder(SurfaceBuilderConfig.CODEC));
	
	public static <SB extends SurfaceBuilder<?>> RegistryObject<SB> registerSurfaceBuilder(String regName, Supplier<SB> surfaceBuilder) {
		RegistryObject<SB> surfaceBuilderObj = SURFACE_BUILDERS.register(regName, surfaceBuilder);
		return surfaceBuilderObj;
	}
	
	public static final class SurfaceBuilderConfigs {
		public static final SurfaceBuilderConfig STALAGMITE_VALLEY = new SurfaceBuilderConfig(Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState());
		public static final SurfaceBuilderConfig STONE = new SurfaceBuilderConfig(Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState());
		public static final SurfaceBuilderConfig GRAVEL = new SurfaceBuilderConfig(Blocks.GRAVEL.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.STONE.defaultBlockState());
		public static final SurfaceBuilderConfig ANDESITE = new SurfaceBuilderConfig(Blocks.ANDESITE.defaultBlockState(), Blocks.ANDESITE.defaultBlockState(), Blocks.STONE.defaultBlockState());
	}

	public static final class ConfiguredSurfaceBuilders {
		public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> STALAGMITE_VALLEY = CASurfaceBuilders.STALAGMITE_VALLEY.get().configured(SurfaceBuilderConfigs.STALAGMITE_VALLEY);

		private static <SC extends ISurfaceBuilderConfig> void register(String key, ConfiguredSurfaceBuilder<SC> builder) {
			WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, ChaosAwakens.prefix(key), builder);
		}

		public static void registerConfiguredSurfaceBuilders() {
			register("stalagmite_valley", STALAGMITE_VALLEY);
		}
	}
}
