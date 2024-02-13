package me.tahacheji.mafana.processor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class WorldManager {

    private String name;
    private String templateWorldName;

    public WorldManager(String name, String templateWorldName) {
        this.name = name;
        this.templateWorldName = templateWorldName;
    }

    public World getWorld() {
        String playerWorldName = name + UUID.randomUUID().toString();
        World existingWorld = Bukkit.getWorld(playerWorldName);

        if (existingWorld != null) {
            return existingWorld;
        }

        File worldFolder = new File(Bukkit.getWorldContainer().getParentFile(), playerWorldName);

        if (!worldFolder.exists()) {
            try {
                copyFolder(new File(templateWorldName), worldFolder);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        World newWorld = Bukkit.createWorld(new WorldCreator(playerWorldName));

        if (newWorld != null) {
            newWorld.setAutoSave(true);
        }

        return newWorld;
    }

    public void copyFolder(File source, File target) throws IOException {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFolder(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public String getTemplateWorldName() {
        return templateWorldName;
    }
}
