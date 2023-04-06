package fr.hyriode.bridger.game.animation.impl;

import fr.hyriode.bridger.game.animation.BridgerFinishAnimation;
import fr.hyriode.bridger.game.blocks.Specification;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FallingBlockAnimation extends BridgerFinishAnimation implements Runnable {

    private static final List<FallingBlock> fallingBlocks = new ArrayList<>();

    public FallingBlockAnimation() {
        super("falling_block", Specification.VIP);
    }

    @Override @SuppressWarnings("deprecation")
    protected void play(List<Location> blockLocs) {
        Iterator<Location> iterator = blockLocs.iterator();
        while (iterator.hasNext()) {
            Location blockLoc = iterator.next();
            if (blockLoc.getBlock().getType() == Material.AIR) {
                iterator.remove();
                continue;
            }
            FallingBlock fallingBlock = blockLoc.getWorld().spawnFallingBlock(blockLoc, blockLoc.getBlock().getType(), blockLoc.getBlock().getData());
            fallingBlocks.add(fallingBlock);
            blockLoc.getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void run() {
        if (fallingBlocks.isEmpty()) return;
        Iterator<FallingBlock> iterator = fallingBlocks.iterator();
        while (iterator.hasNext()) {
            FallingBlock fallingBlock = iterator.next();
            if (fallingBlock.getLocation().getY() < 10) {
                fallingBlock.remove();
                iterator.remove();
            }
        }
    }
}