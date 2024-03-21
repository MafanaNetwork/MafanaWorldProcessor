package me.tahacheji.mafana.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtil {

    public void delete(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if(files == null) return;
            for(File child : files) {
                delete(child);
            }
        }
        file.delete();
    }

    public void deleteWorldFolder(File worldFolder) {
        if (worldFolder.exists()) {
            // Delete the world folder and all its contents recursively
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            worldFolder.delete(); // Delete the empty directory
        }
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

}
