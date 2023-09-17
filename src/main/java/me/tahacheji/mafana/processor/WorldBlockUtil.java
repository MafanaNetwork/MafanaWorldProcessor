package me.tahacheji.mafana.processor;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class WorldBlockUtil {

    public String encryptDungeonWorldBlocks(List<WorldBlock> blocks) {
        List<String> list = new ArrayList<>();
        for(WorldBlock worldBlock : blocks) {
            String material = worldBlock.getMaterial().name();
            String x = String.valueOf(worldBlock.getX());
            String y = String.valueOf(worldBlock.getY());
            String z = String.valueOf(worldBlock.getZ());
            list.add(material + "|" + x + "|" + y + "|" + z);
        }
        return new EncryptionUtil().encryptList(list);
    }

    public List<WorldBlock> decryptDungeonWorldBlocks(String encryptedData) {
        try {
            List<String> decryptedList = new EncryptionUtil().decryptToList(encryptedData);

            List<WorldBlock> worldBlocks = new ArrayList<>();

            for (String decryptedMessage : decryptedList) {
                String[] parts = decryptedMessage.split("\\|");
                if (parts.length >= 4) {
                    Material material = Material.valueOf(parts[0]);
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);
                    worldBlocks.add(new WorldBlock(material, x, y, z));
                }
            }
            return worldBlocks;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
