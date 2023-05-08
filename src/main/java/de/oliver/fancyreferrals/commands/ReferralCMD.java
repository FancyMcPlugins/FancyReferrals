package de.oliver.fancyreferrals.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyreferrals.ReferralManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ReferralCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Stream.concat(
                    Stream.of("top"),
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName))
                    .filter(input -> input.startsWith(args[0]))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if(args.length < 1){
            MessageHelper.error(sender, "Correct usage: /Referral <player | top>");
            return false;
        }

        if(args[0].equalsIgnoreCase("top")){

            for (int i = 1; i <= 10; i++) {
                Map.Entry<String, Integer> entry = ReferralManager.getTopPlayer(i);
                MessageHelper.info(p, "#" + i + " - " + entry.getKey() + " (" + entry.getValue() + ")");
            }

            return true;
        }

        ReferralManager.refer(p, args[0]);

        return false;
    }
}
