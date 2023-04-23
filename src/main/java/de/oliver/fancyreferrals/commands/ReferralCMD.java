package de.oliver.fancyreferrals.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyreferrals.ReferralManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReferralCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if(args.length < 1){
            MessageHelper.error(sender, "Correct usage: /Referral <player>");
            return false;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if(t == null){
            MessageHelper.error(p, "The player '" + args[0] + "' is not online");
            return false;
        }

        ReferralManager.refer(p, t);

        return false;
    }
}
