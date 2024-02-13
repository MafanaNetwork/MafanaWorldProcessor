package me.tahacheji.mafana.processors;


import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.commandExecutor.paramter.Processor;
import me.tahacheji.mafana.processor.Build;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuildProcessor extends Processor<Build> {

    @Override
    public Build process(CommandSender commandSender, String s) {
        return new Build(MafanaWorldProcessor.getInstance().getWorldBlockData().getBuildFromName(s), false);
    }

    public List<String> tabComplete(CommandSender sender, String supplied) {
        return MafanaWorldProcessor.getInstance().getWorldBlockData().getAllNames()
                .stream()
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase().startsWith(supplied.toLowerCase()))
                .collect(Collectors.toList());
    }
}
