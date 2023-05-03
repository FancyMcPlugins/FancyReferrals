package de.oliver.fancyreferrals.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancyreferrals.FancyReferrals;
import de.oliver.fancyreferrals.ReferralManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
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
            Database database = FancyReferrals.getInstance().getDatabase();
            ResultSet res = database.executeQuery("SELECT referred, COUNT(*) as count FROM referrals ORDER BY count DESC LIMIT 10");
            if(res == null){
                MessageHelper.error(p, "Could not fetch data");
                return false;
            }

            try {
                int i = 0;
                while (res.next()) {
                    i++;
                    UUID uuid = UUID.fromString(res.getString("referred"));
                    String name = UUIDFetcher.getName(uuid);
                    int count = res.getInt("count");
                    MessageHelper.info(p, "#" + i + " - " + name + " (" + count + ")");
                }
            }catch (SQLException e){
                MessageHelper.error(p, "Could not fetch data");
                return false;
            }

            return true;
        }

        ReferralManager.refer(p, args[0]);

        return false;
    }
}
