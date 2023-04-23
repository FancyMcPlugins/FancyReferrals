package de.oliver.fancyreferrals;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancyreferrals.commands.FancyReferralsCMD;
import de.oliver.fancyreferrals.commands.ReferralCMD;
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

        getCommand("FancyReferrals").setExecutor(new FancyReferralsCMD());
        getCommand("Referral").setExecutor(new ReferralCMD());
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
