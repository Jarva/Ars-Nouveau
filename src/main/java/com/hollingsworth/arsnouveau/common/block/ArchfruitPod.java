package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.common.block.SconceBlock.LIGHT_LEVEL;

public class ArchfruitPod extends CocoaBlock implements ILightable {
    public Supplier<Block> surviveBlock;
    protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.box(11.0D, 7.0D, 6.0D, 15.0D, 11.0D, 10.0D),
            Block.box(9.0D, 5.0D, 5.0D, 15.0D, 11.0D, 11.0D),
            Block.box(7.0D, 3.0D, 4.0D, 15.0D, 11.0D, 12.0D)};
    protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 1.0D, 10.0D, 11.0D, 5.0D),
            Block.box(5.0D, 5.0D, 1.0D, 11.0D, 11.0D, 7.0D),
            Block.box(4.0D, 3.0D, 1.0D, 12.0D, 11.0D, 9.0D)};
    protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 11.0D, 10.0D, 11.0D, 15.0D),
            Block.box(5.0D, 5.0D, 9.0D, 11.0D, 11.0D, 15.0D),
            Block.box(4.0D, 3.0D, 7.0D, 12.0D, 11.0D, 15.0D)};

    protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.box(1.0D, 7.0D, 6.0D, 5.0D, 11.0D, 10.0D),
            Block.box(1.0D, 5.0D, 5.0D, 7.0D, 11.0D, 11.0D),
            Block.box(1.0D, 3.0D, 4.0D, 9.0D, 11.0D, 12.0D)};


    public ArchfruitPod(Supplier<Block> surviveBlock) {
       this(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().strength(0.2F, 3.0F).sound(SoundType.WOOD).noOcclusion().lightLevel((b) -> b.getValue(LIGHT_LEVEL)));
       this.surviveBlock = surviveBlock;
    }

    public ArchfruitPod(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AGE, 0)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.relative(pState.getValue(FACING)));
        return blockstate.getBlock() == this.surviveBlock.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int i = pState.getValue(AGE);
        switch (pState.getValue(FACING)) {
            case SOUTH:
                return SOUTH_AABB[i];
            case WEST:
                return WEST_AABB[i];
            case EAST:
                return EAST_AABB[i];
            default:
                return NORTH_AABB[i];
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AGE, LIGHT_LEVEL);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        if (rayTraceResult instanceof BlockHitResult blockHitResult) {
            BlockState state = world.getBlockState(blockHitResult.getBlockPos());
            world.setBlock(blockHitResult.getBlockPos(), state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 8 - stats.getBuffCount(AugmentDampen.INSTANCE)), 8)), 3);
            world.sendBlockUpdated(blockHitResult.getBlockPos(), state,
                    state.setValue(SconceBlock.LIGHT_LEVEL,  Math.min(Math.max(0, 8 - stats.getBuffCount(AugmentDampen.INSTANCE)), 8)), 3);
        }
    }
}