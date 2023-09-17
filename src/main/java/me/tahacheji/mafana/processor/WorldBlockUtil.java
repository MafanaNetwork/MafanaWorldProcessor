package me.tahacheji.mafana.processor;

import org.bukkit.Material;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WorldBlockUtil {

    public String compressDungeonWorldBlocks(List<WorldBlock> blocks) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            for (WorldBlock worldBlock : blocks) {
                String material = worldBlock.getMaterial().name();
                String x = String.valueOf(worldBlock.getX());
                String y = String.valueOf(worldBlock.getY());
                String z = String.valueOf(worldBlock.getZ());
                String entry = material + "|" + x + "|" + y + "|" + z + "\n";
                gzipOutputStream.write(entry.getBytes());
            }
        }
        return byteArrayOutputStream.toString("UTF-8");
    }

    public List<WorldBlock> decompressDungeonWorldBlocks(String compressedData) throws IOException {
        List<WorldBlock> worldBlocks = new ArrayList<>();
        byte[] compressedBytes = compressedData.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder entry = new StringBuilder();

            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                entry.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
                while (entry.toString().contains("\n")) {
                    int endIndex = entry.indexOf("\n");
                    String line = entry.substring(0, endIndex);
                    entry.delete(0, endIndex + 1);

                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        Material material = Material.valueOf(parts[0]);
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int z = Integer.parseInt(parts[3]);
                        worldBlocks.add(new WorldBlock(material, x, y, z));
                    }
                }
            }
        }
        return worldBlocks;
    }
}
