package me.developful.village;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.developful.village.Village.VILLAGERS;

public class Job implements TabCompleter, CommandExecutor {

    public String type;
    public float level = 0;
    public static final HashMap<String, Material> JOB_MATERIALS = new HashMap<>();

    private static final List<String> CUSTOMIZE_ARGUMENTS = Arrays.asList("set", "promote");
    private static final List<String> OP_CUSTOMIZE_ARGUMENTS = Arrays.asList("set", "promote", "create", "delete");

    public Job(String type) {
        this.type = type.toUpperCase();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("village.modify"))
                return OP_CUSTOMIZE_ARGUMENTS;

            return CUSTOMIZE_ARGUMENTS;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "delete":
                case "set":
                    return JOB_MATERIALS.keySet().stream().map(String::toLowerCase).toList();

                default: return null;
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "create": return Arrays.stream(Material.values()).map(x -> x.toString().toLowerCase()).toList();
                default: return null;
            }
        }

        return null;
    }

    private void incompleteCommand(Player player, String label, String command) {
        player.sendMessage(Component.text("Unknown or incomplete command, see below for error", NamedTextColor.RED));
        player.sendMessage(
                Component.text(label.isEmpty() ? "" : label + " ", NamedTextColor.GRAY)
                        .append(Component.text(command, NamedTextColor.RED, TextDecoration.UNDERLINED))
                        .append(Component.text("<--[HERE]", NamedTextColor.RED)));
    }

    private void incorrectArgument(Player player, String label, String command) {
        player.sendMessage(Component.text("Incorrect argument for command", NamedTextColor.RED));
        player.sendMessage(
                Component.text(label + " ", NamedTextColor.GRAY)
                        .append(Component.text(command, NamedTextColor.RED, TextDecoration.UNDERLINED))
                        .append(Component.text("<--[HERE]", NamedTextColor.RED)));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return false;

        if (args.length == 0) {
            incompleteCommand(player, "", label);
            return false;
        }

        if (!OP_CUSTOMIZE_ARGUMENTS.contains(args[0].toLowerCase())) {
            incorrectArgument(player, label, args[0]);
            return false;
        }

        if (!CUSTOMIZE_ARGUMENTS.contains(args[0].toLowerCase()) && !player.hasPermission("village.modify")) {
            incorrectArgument(player, label, args[0]);
            return false;
        }

        return switch (args[0].toLowerCase()) {
            case "set" -> commandSet(player, label, args);
            case "levelup" -> commandLevelUp(player, label, args);
            case "promote" -> commandPromote(player, label, args);
            case "create" -> commandCreate(player, label, args);
            case "delete" -> commandDelete(player, label, args);
            default -> false;
        };
    }

    private boolean deleteAndSetCommandsWarnings(Player player, String label, String[] args) {
        if (args.length == 1) {
            incompleteCommand(player, label, args[0]);
            return true;
        }

        if (!JOB_MATERIALS.containsKey(args[1].toUpperCase())) {
            incorrectArgument(
                    player,
                    label + " " + args[0],
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length))
            );

            return true;
        }

        if (args.length > 2) {
            incorrectArgument(
                    player,
                    label + " " + args[0] + " " + args[1],
                    String.join(" ", Arrays.copyOfRange(args, 2, args.length))
            );

            return true;
        }

        return false;
    }

    private boolean commandSet(Player player, String label, String[] args) {
        if (deleteAndSetCommandsWarnings(player, label, args))
            return false;

        VILLAGERS.get(player.getName()).job = new Job(args[1].toUpperCase());
        player.sendMessage(Component.text("Set role to: " + args[1].toLowerCase() + "!", NamedTextColor.GREEN));

        return true;
    }

    private boolean commandDelete(Player player, String label, String[] args) {
        if (!player.hasPermission("village.modify")) {
            player.sendMessage(Component.text("Insufficient permissions to run this command!", NamedTextColor.RED));
            return false;
        }

        if (deleteAndSetCommandsWarnings(player, label, args))
            return false;

        JOB_MATERIALS.remove(args[1].toUpperCase());
        for (Map.Entry<String, PlayerVillager> entry : VILLAGERS.entrySet()) {
            var villager = entry.getValue();

            if (villager.job.type.equals(args[1].toUpperCase()))
                villager.job = new Job("NONE");
        }

        player.sendMessage(Component.text("Job '" + args[1].toLowerCase() + "' was removed!", NamedTextColor.GREEN));
        return true;
    }

    private boolean commandCreate(Player player, String label, String[] args) {
        if (!player.hasPermission("village.modify")) {
            player.sendMessage(Component.text("Insufficient permissions to run this command!", NamedTextColor.RED));
            return false;
        }

        if (args.length == 1) {
            incompleteCommand(player, label, args[0]);
            return false;
        }

        if (JOB_MATERIALS.containsKey(args[1].toUpperCase())) {
            player.sendMessage(Component.text("Job " + args[1].toLowerCase() + " already exists!", NamedTextColor.RED));
            return false;
        }

        if (args.length == 2) {
            incompleteCommand(player, label + " " + args[0], args[1]);
            return false;
        }

        if (args.length > 3) {
            incorrectArgument(
                    player,
                    label + " " + args[0] + " " + args[1] + " " + args[2],
                    String.join(" ", Arrays.copyOfRange(args, 3, args.length))
            );

            return false;
        }

        var job = args[1].toUpperCase();
        Material material;
        try {
            material = Material.valueOf(args[2].toUpperCase());
        }
        catch (IllegalArgumentException e) {
            incorrectArgument(
                    player,
                    label + " " + args[0] + " " + args[1],
                    String.join(" ", Arrays.copyOfRange(args, 2, args.length))
            );

            return false;
        }


        JOB_MATERIALS.put(job, material);
        player.sendMessage(Component.text("Job '" + job.toLowerCase() + "' was created!", NamedTextColor.GREEN));
        return true;
    }

    private boolean commandPromote(Player player, String label, String[] args) {
        if (args.length > 1) {
            incorrectArgument(
                    player,
                    label + " " + args[0],
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length))
            );

            return false;
        }

        var villager = VILLAGERS.get(player.getName());
        Material neededMaterial = JOB_MATERIALS.get(villager.job.type);
        var activeItem = player.getInventory().getItemInMainHand();

        if (neededMaterial != activeItem.getType()) {
            if (neededMaterial == null) {
                player.sendMessage(Component.text("Pick a job with '/job set'!", NamedTextColor.RED));
                return false;
            }

            if (activeItem.getType() == Material.AIR) {
                player.sendMessage(
                        Component.text("Expected item ", NamedTextColor.RED)
                                .append(Component.text(neededMaterial.name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED))
                                .append(Component.text(" to be equipped in the main hand!", NamedTextColor.RED)));
                return false;
            }

            player.sendMessage(
                    Component.text("Invalid item to upgrade with! Needed ", NamedTextColor.RED)
                            .append(Component.text(neededMaterial.name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED))
                            .append(Component.text(" , but was given ", NamedTextColor.RED))
                            .append(Component.text(activeItem.getType().name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED)));
            return false;
        }

        var qty = activeItem.getAmount();
        var points = qty / (5 + (villager.job.level / 3));

        activeItem.setAmount(0);
        villager.job.level += points;

        player.sendMessage(Component.text("Promoted level by " + String.format("%.2f", points) + " points!", NamedTextColor.GREEN));
        return true;
    }

    private boolean commandLevelUp(Player player, String label, String[] args) {
        // todo: maybe refactor this bc its the same as promote
        if (args.length > 1) {
            incorrectArgument(
                    player,
                    label + " " + args[0],
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length))
            );

            return false;
        }

        var villager = VILLAGERS.get(player.getName());
        Material neededMaterial = JOB_MATERIALS.get(villager.job.type);
        var activeItem = player.getInventory().getItemInMainHand();

        if (neededMaterial != activeItem.getType()) {
            if (neededMaterial == null) {
                player.sendMessage(Component.text("Pick a job with '/job set'!", NamedTextColor.RED));
                return false;
            }

            if (activeItem.getType() == Material.AIR) {
                player.sendMessage(
                        Component.text("Expected item ", NamedTextColor.RED)
                                .append(Component.text(neededMaterial.name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED))
                                .append(Component.text(" to be equipped in the main hand!", NamedTextColor.RED)));
                return false;
            }

            player.sendMessage(
                    Component.text("Invalid item to add to balance with! Needed ", NamedTextColor.RED)
                            .append(Component.text(neededMaterial.name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED))
                            .append(Component.text(" , but was given ", NamedTextColor.RED))
                            .append(Component.text(activeItem.getType().name().toLowerCase(), NamedTextColor.RED, TextDecoration.UNDERLINED)));
            return false;
        }

        var qty = activeItem.getAmount();
        var points = qty * (villager.job.level / 6);

        activeItem.setAmount(0);
        villager.balance += points;

        player.sendMessage(Component.text("Added to balance " + String.format("%.2f", points) + " points!", NamedTextColor.GREEN));
        return true;
    }
}
