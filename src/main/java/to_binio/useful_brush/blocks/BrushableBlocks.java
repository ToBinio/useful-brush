package to_binio.useful_brush.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.event.BrushBlockEvent;

public class BrushableBlocks {

    public static void brushBlock(World world, ItemStack stack, PlayerEntity playerEntity,
            HitResult hitResult, BlockPos blockPos, BlockState blockState) {
        var brushableBlock = UsefulBrush.BRUSHABLE_BLOCKS.get(blockState.getBlock());

        if (brushableBlock != null) {
            brushBrushableBlock(world, stack, playerEntity, hitResult, blockPos, brushableBlock, blockState);
        } else {
            clearBlockBreakingInfo(world, playerEntity);

            ActionResult brushResult = BrushBlockEvent.getEvent(blockState.getBlock())
                    .invoker()
                    .brush(playerEntity, blockPos);

            if (brushResult == ActionResult.SUCCESS && !world.isClient()) {
                EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.damage(1, playerEntity, ((userx) -> {
                    userx.sendEquipmentBreakStatus(equipmentSlot);
                }));
            }
        }
    }

    public static void brushBrushableBlock(World world, ItemStack stack, PlayerEntity player,
            HitResult hitResult, BlockPos blockPos, BrushableBlockEntry blockEntry, BlockState blockState) {

        world.setBlockBreakingInfo(player.getId(), blockPos, Math.round(((float) (blockEntry.brushCount() - BrushBlockCounter.get(blockPos, player.getId(), blockEntry.brushCount(), world.isClient())) / blockEntry.brushCount()) * 10));

        if (BrushBlockCounter.decreaseCount(blockPos, player.getId(), blockEntry.brushCount(), world.isClient()) != 0)
            return;

        clearBlockBreakingInfo(world, player);

        if (world.isClient())
            return;

        if (blockEntry.block() == Blocks.AIR) {
            world.breakBlock(blockPos, false);
        } else {
            world.setBlockState(blockPos, blockEntry.block().getStateWithProperties(blockState));
        }

        EquipmentSlot equipmentSlot = stack.equals(player.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        stack.damage(1, player, (userx) -> {
            userx.sendEquipmentBreakStatus(equipmentSlot);
        });

        if (blockEntry.lootTable() == null)
            return;

        var lootTable = world.getServer().getLootManager().getLootTable(blockEntry.lootTable());

        if (lootTable != LootTable.EMPTY) {
            LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) world)).add(LootContextParameters.ORIGIN, blockPos.toCenterPos())
                    .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                    .add(LootContextParameters.BLOCK_STATE, blockState);

            LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.BLOCK);
            lootTable.generateLoot(lootContextParameterSet, 0L, itemStack -> {

                UsefulBrush.LOGGER.info(itemStack.getItem().getName().toString());

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

    public static void clearBlockBreakingInfo(World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {

            if (BrushBlockCounter.clear(player.getId(), world.isClient())) {
                world.setBlockBreakingInfo(user.getId(), BlockPos.ORIGIN, -1);
            }
        }
    }
}
