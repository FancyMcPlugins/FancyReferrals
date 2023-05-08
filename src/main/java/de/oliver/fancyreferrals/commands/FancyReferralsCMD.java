package de.oliver.fancyreferrals.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyreferrals.FancyReferrals;
import de.oliver.fancyreferrals.ReferralManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class FancyReferralsCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Arrays.asList("reload", "refresh", "version");
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length >= 1 && args[0].equalsIgnoreCase("reload")){
            FancyReferrals.getInstance().getFancyReferralsConfig().reload();
            ReferralManager.loadFromDatabase();
            MessageHelper.success(sender, "Reloaded the config");
            return true;
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("version")){
            String currentVersion = FancyReferrals.getInstance().getDescription().getVersion();
            MessageHelper.info(sender, "FancyReferrals version: " + currentVersion);
            return true;
        } else if(args.length >= 1 && args[0].equalsIgnoreCase("refresh")){
            ReferralManager.refreshTopPlayerCache();
            MessageHelper.success(sender, "Refreshed the top 10 players list");
        } else {
            MessageHelper.info(sender, "/FancyReferrals <version | reload | refresh>");
        }

        return false;
    }
}
