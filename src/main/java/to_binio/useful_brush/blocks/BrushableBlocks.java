package to_binio.useful_brush.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.event.BrushBlockEvent;

import static to_binio.useful_brush.BrushUtil.handleBrushEvent;

public class BrushableBlocks {

    public static void brush(World world, ItemStack stack, PlayerEntity playerEntity,
            HitResult hitResult, BlockPos blockPos, BlockState blockState) {
        var brushableBlock = UsefulBrush.BASIC_BRUSHABLE_BLOCKS.get(blockState.getBlock());

        BrushCounter.brushBlock(blockPos, playerEntity.getId(), world);

        if (brushableBlock != null) {
            brushBrushable(world, stack, playerEntity, hitResult, blockPos, brushableBlock, blockState);
        } else {
            ActionResult brushResult = BrushBlockEvent.getEvent(blockState.getBlock())
                    .invoker()
                    .brush(playerEntity, blockPos);

            handleBrushEvent(world, stack, playerEntity, brushResult);
        }
    }

    private static void brushBrushable(World world, ItemStack stack, PlayerEntity player,
            HitResult hitResult, BlockPos blockPos, BrushableBlockEntry blockEntry, BlockState blockState) {

        world.setBlockBreakingInfo(player.getId(), blockPos, Math.round(((float) BrushCounter.get(player.getId(), world.isClient()) / blockEntry.brushCount()) * 10));

        if (BrushCounter.get(player.getId(), world.isClient) < blockEntry.brushCount())
            return;

        BrushCounter.clear(player.getId(), world);

        if (world.isClient())
            return;

        if (blockEntry.block() == Blocks.AIR) {
            world.breakBlock(blockPos, false);
        } else {
            world.setBlockState(blockPos, blockEntry.block().getStateWithProperties(blockState));
        }

        EquipmentSlot equipmentSlot = stack.equals(player.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        stack.damage(1, player, equipmentSlot);

        if (blockEntry.lootTable() == null || world.getServer() == null)
            return;

        var lootTable = world.getServer()
                .getReloadableRegistries()
                .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, blockEntry.lootTable()));

        if (lootTable != LootTable.EMPTY) {
            LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) world))
                    .add(LootContextParameters.ORIGIN, blockPos.toCenterPos())
                    .add(LootContextParameters.TOOL, stack)
                    .add(LootContextParameters.BLOCK_STATE, blockState)
                    .add(LootContextParameters.THIS_ENTITY, player);

            LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.BLOCK);
            lootTable.generateLoot(lootContextParameterSet, 0L, itemStack -> {

                Vec3d pos = hitResult.getPos();
                Vec3d center = blockPos.toCenterPos();

                Vec3d offset = pos.subtract(center);

                Vec3d spawnPos = pos.add(offset.normalize().multiply(0.2));

                ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            });
        } else {
            UsefulBrush.LOGGER.error("Could not find loot_table '%s'".formatted(blockEntry.lootTable()));
        }
    }

    public static void clear(int playerID, World world) {
        world.setBlockBreakingInfo(playerID, BlockPos.ORIGIN, -1);
    }
}
