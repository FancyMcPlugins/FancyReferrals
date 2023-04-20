package de.oliver.fancyreferrals;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyreferrals.commands.FancyReferralsCMD;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyReferrals extends JavaPlugin {

    private static FancyReferrals instance;

    public FancyReferrals() {
        instance = this;
    }

    @Override
    public void onEnable() {
        MessageHelper.pluginName = getDescription().getName();

        getCommand("FancyReferrals").setExecutor(new FancyReferralsCMD());
    }

    @Override
    public void onDisable() {

    }

    public static FancyReferrals getInstance() {
        return instance;
    }
}
