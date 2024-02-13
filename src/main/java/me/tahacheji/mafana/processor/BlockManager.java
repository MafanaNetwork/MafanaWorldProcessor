package me.tahacheji.mafana.processor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BlockManager {

    private List<WorldBlock> blockList = new ArrayList<>();
    private Player targetPlayer;

    public BlockManager() {

    }

    public BlockManager(List<WorldBlock> blockList) {
        this.blockList = blockList;
    }

    public BlockManager(Player targetPlayer, List<WorldBlock> blockList) {
        this.blockList = blockList;
        this.targetPlayer = targetPlayer;
    }

    public void showBlocks(Player targetPlayer) {
        for (WorldBlock block : blockList) {
            showBlock(targetPlayer, block);
        }
    }

    public void hideBlocks(Player targetPlayer) {
        for (WorldBlock block : blockList) {
            hideBlock(targetPlayer, block);
        }
    }


    private void showBlock(Player targetPlayer, WorldBlock block) {
        WrappedBlockData wrappedBlockData = WrappedBlockData.createData(block.getMaterial());
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        packet.getBlockData().write(0, wrappedBlockData);

        ProtocolLibrary.getProtocolManager().sendServerPacket(targetPlayer, packet);
    }

    private void hideBlock(Player targetPlayer, WorldBlock block) {
        WrappedBlockData wrappedBlockData = WrappedBlockData.createData(Material.AIR);
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        packet.getBlockData().write(0, wrappedBlockData);

        ProtocolLibrary.getProtocolManager().sendServerPacket(targetPlayer, packet);
    }

    public boolean isBlockInChunk(Chunk chunk) {
        for (WorldBlock block : blockList) {
            Location location = new Location(chunk.getWorld(), block.getX(), block.getY(), block.getZ());
            if (chunk.equals(location.getChunk())) {
                return true;
            }
        }
        return false;
    }

    public List<WorldBlock> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<WorldBlock> blockList) {
        this.blockList = blockList;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
}

