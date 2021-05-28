package io.github.chaosawakens.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;
import java.util.function.Supplier;

import io.github.chaosawakens.common.registry.CABlocks;

public class CAOreBlock extends Block {
	private Supplier<Integer> expFormula = () -> {
		return 0;
	};
    private boolean isFossilisedOre = false;

    public CAOreBlock(AbstractBlock.Properties properties, boolean fossilised) {
        super(properties);
        this.isFossilisedOre = fossilised;
    }

    public CAOreBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    protected int getExperience(Random rand) {
        if (CABlocks.AMETHYST_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 3, 7);
        } else if (CABlocks.RUBY_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 4, 9);
        } else if (CABlocks.TIGERS_EYE_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 4, 8);
        } else if (CABlocks.BLOODSTONE_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 2, 5);
        } else if (CABlocks.SUNSTONE_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 3, 6);
        } else if (CABlocks.SALT_ORE.get().equals(this)) {
            return MathHelper.nextInt(rand, 0, 2);
        }
        return this.isFossilisedOre ? MathHelper.nextInt(rand, 0, 2) : 0;
    }
    
    public CAOreBlock withExpDrop(Supplier<Integer> expFormula) {
    	this.expFormula = expFormula;
    	return this;
    }
    
    public CAOreBlock withFossilExp() {
    	this.expFormula = () -> {
    		return MathHelper.nextInt( new Random(), 0, 2);
    	};
    	return this;
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0  ? this.expFormula.get() : 0;
    }
}
