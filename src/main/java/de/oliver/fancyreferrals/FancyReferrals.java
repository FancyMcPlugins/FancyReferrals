package de.oliver.fancyreferrals;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancyreferrals.commands.FancyReferralsCMD;
import de.oliver.fancyreferrals.commands.ReferralCMD;
import de.oliver.fancyreferrals.placeholders.FancyReferralsPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyReferrals extends JavaPlugin {

    private static FancyReferrals instance;
    private FancyReferralsConfig config;
    private Database database;
    private boolean usingPlaceholderAPI;

    public FancyReferrals() {
        instance = this;
        config = new FancyReferralsConfig();
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(instance);

        PluginManager pluginManager = Bukkit.getPluginManager();

        usingPlaceholderAPI = pluginManager.isPluginEnabled("PlaceholderAPI");

        config.reload();
        database = config.getDatabase();
        if(database == null){
            getLogger().warning("Please provide database information in the config");
            pluginManager.disablePlugin(instance);
            return;
        }

        if (!database.connect()) {
            getLogger().warning("Could not connect to database");
            pluginManager.disablePlugin(instance);
            return;
        } else {
            boolean success = database.executeNonQuery("CREATE TABLE IF NOT EXISTS referrals(" +
                    "referrer VARCHAR(255)," +
                    "referred VARCHAR(255)," +
                    "timestamp LONG," +
                    "PRIMARY KEY(referrer, referred)" +
                    ")");
            if(!success){
                getLogger().warning("Could not create the 'referrals' table");
                pluginManager.disablePlugin(instance);
                return;
            }
        }

        ReferralManager.loadFromDatabase();
        ReferralManager.refreshTopPlayerCache();

        int refreshTopPlayersInterval = config.getRefreshTopPlayersInterval();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, ReferralManager::refreshTopPlayerCache, 20L*60*refreshTopPlayersInterval, 20L*60*refreshTopPlayersInterval);

        getCommand("FancyReferrals").setExecutor(new FancyReferralsCMD());
        getCommand("Referral").setExecutor(new ReferralCMD());

        if(usingPlaceholderAPI){
            new FancyReferralsPlaceholderExpansion().register();
        }
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
