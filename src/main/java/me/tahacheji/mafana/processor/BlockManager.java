package me.tahacheji.mafana.processor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BlockManager {

    private Player targetPlayer;
    private List<WorldBlock> blockList;

    public BlockManager(Player targetPlayer, List<WorldBlock> blockList) {
        this.targetPlayer = targetPlayer;
        this.blockList = blockList;
    }

    public void showBlocks() {
        for (WorldBlock block : blockList) {
            showBlock(block);
        }
    }

    public void hideBlocks() {
        for (WorldBlock block : blockList) {
            hideBlock(block);
        }
    }

    private void showBlock(WorldBlock block) {

        WrappedBlockData wrappedBlockData = WrappedBlockData.createData(block.getMaterial());
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        packet.getBlockData().write(0, wrappedBlockData);

        ProtocolLibrary.getProtocolManager().sendServerPacket(targetPlayer, packet);
    }

    private void hideBlock(WorldBlock block) {
        // Create and send a packet to replace the block with air.
        WrappedBlockData wrappedBlockData = WrappedBlockData.createData(Material.AIR);
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        packet.getBlockData().write(0, wrappedBlockData);

        ProtocolLibrary.getProtocolManager().sendServerPacket(targetPlayer, packet);
    }

    public List<WorldBlock> getBlockList() {
        return blockList;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
}

