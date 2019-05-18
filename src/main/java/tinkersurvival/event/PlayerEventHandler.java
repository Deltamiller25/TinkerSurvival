package tinkersurvival.event;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tinkersurvival.client.sound.Sounds;
import tinkersurvival.config.ConfigHandler;
import tinkersurvival.world.TinkerSurvivalWorld;

public class PlayerEventHandler {

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event) {
        // Control for flint shard creation
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (event.getItemStack().getItem() == Items.FLINT
                && world.getBlockState(pos).getMaterial() == Material.ROCK
                && state.isFullCube()) {
            if (!world.isRemote){
                if (Math.random() < 0.7) {
                    if (Math.random() < ConfigHandler.balance.FLINT_CHANCE) {
                        // Create flint shard
                        ItemStack stack = new ItemStack(TinkerSurvivalWorld.flintShard, 2);
                        EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
                        world.spawnEntity(item);
                    }

                    // Remove one flint
                    ItemStack stack2 = event.getItemStack();
                    stack2.shrink(1);
                    if (stack2.getCount() == 0) {
                        stack2 = ItemStack.EMPTY;
                    }
                    event.getEntityPlayer().setHeldItem(event.getHand(), stack2);
                }
            }
            Sounds.play(event.getEntityPlayer(), Sounds.FLINT_KNAPPING, 0.8F, 1.0F);
        }
    }
}
