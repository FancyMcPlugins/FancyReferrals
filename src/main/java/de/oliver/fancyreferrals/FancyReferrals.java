package de.oliver.fancyreferrals;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancyreferrals.commands.FancyReferralsCMD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyReferrals extends JavaPlugin {

    private static FancyReferrals instance;
    private FancyReferralsConfig config;
    private Database database;

    public FancyReferrals() {
        instance = this;
        config = new FancyReferralsConfig();
    }

    @Override
    public void onEnable() {
        MessageHelper.pluginName = getDescription().getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        config.reload();
        database = config.getDatabase();
        if(database == null){
            getLogger().warning("Please provide database information in the config");
            pluginManager.disablePlugin(instance);
            return;
        }

        if (!database.connect()) {
            getLogger().warning("Could not connect to database.");
            pluginManager.disablePlugin(instance);
            return;
        }

        getCommand("FancyReferrals").setExecutor(new FancyReferralsCMD());
    }

    @Override
    public void onDisable() {

    }

    public FancyReferralsConfig getFancyReferralsConfig(){
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public static FancyReferrals getInstance() {
        return instance;
    }
}
