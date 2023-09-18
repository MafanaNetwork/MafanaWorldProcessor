package me.tahacheji.mafana.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WorldBlockUtil {

    public List<WorldBlock> decompressJsonToWorldBlocks(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<WorldBlock>>() {}.getType());
    }

    public String compressWorldBlocksToJson(List<WorldBlock> worldBlocks) {
        Gson gson = new Gson();
        return gson.toJson(worldBlocks);
    }

    public void copyWorld(String worldName, File fileLocation) throws IOException {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        File targetFolder = new File(fileLocation.getPath());

        if (!worldFolder.exists() || !worldFolder.isDirectory()) {
            throw new IllegalArgumentException("The specified world does not exist or is not a directory.");
        }

        if (targetFolder.exists() && !targetFolder.isDirectory()) {
            throw new IllegalArgumentException("The target location is not a directory.");
        }

        // Use Java NIO to copy the world folder and its contents recursively
        Path sourcePath = worldFolder.toPath();
        Path targetPath = targetFolder.toPath();

        EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);

        Files.walkFileTree(sourcePath, options, Integer.MAX_VALUE, new CopyWorldFileVisitor(sourcePath, targetPath));
    }

    private static class CopyWorldFileVisitor extends java.nio.file.SimpleFileVisitor<Path> {
        private final Path sourcePath;
        private final Path targetPath;

        CopyWorldFileVisitor(Path sourcePath, Path targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
            Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
            Files.createDirectories(targetDir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
            Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
            return FileVisitResult.CONTINUE;
        }
    }
}
