package me.tahacheji.mafana.processors;

import me.tahacheji.mafana.commandExecutor.paramter.Processor;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeProcessor extends Processor<Biome> {

    @Override
    public Biome process(CommandSender commandSender, String s) {
        Biome biome = null;
        try {
            biome = Biome.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage("Invalid biome name.");
        }
        return biome;
    }

    public List<String> tabComplete(CommandSender sender, String supplied) {
        List<String> biomeNames = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            biomeNames.add(biome.name().toLowerCase());
        }
        return biomeNames
                .stream()
                .filter(name -> name.toLowerCase().startsWith(supplied.toLowerCase()))
                .collect(Collectors.toList());
    }
}
