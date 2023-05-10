package de.oliver.fancyreferrals;

import de.oliver.fancylib.ConfigHelper;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancylib.databases.SqliteDatabase;
import org.bukkit.configuration.file.FileConfiguration;

public class FancyReferralsConfig {

    private DatabaseType dbType;
    private String dbHost;
    private String dbPort;
    private String dbDatabase;
    private String dbUsername;
    private String dbPassword;
    private String dbFile;
    private int refreshTopPlayersInterval;
    private String successMessage;

    public void reload(){
        FancyReferrals.getInstance().reloadConfig();
        FileConfiguration config = FancyReferrals.getInstance().getConfig();

        dbType = DatabaseType.getByIdentifier((String) ConfigHelper.getOrDefault(config, "database.type", "sqlite"));
        if(dbType == null){
            FancyReferrals.getInstance().getLogger().warning("Invalid database type provided in config");
        }

        dbHost = (String) ConfigHelper.getOrDefault(config, "database.mysql.host", "localhost");
        dbPort = (String) ConfigHelper.getOrDefault(config, "database.mysql.port", "3306");
        dbDatabase = (String) ConfigHelper.getOrDefault(config, "database.mysql.database", "fancyreferrals");
        dbUsername = (String) ConfigHelper.getOrDefault(config, "database.mysql.username", "root");
        dbPassword = (String) ConfigHelper.getOrDefault(config, "database.mysql.password", "");
        dbFile = (String) ConfigHelper.getOrDefault(config, "database.sqlite.file_path", "database.db");
        dbFile = "plugins/FancyReferrals/" + dbFile;

        refreshTopPlayersInterval = (Integer) ConfigHelper.getOrDefault(config, "refresh_top_players_interval", 30);
        successMessage = (String) ConfigHelper.getOrDefault(config, "successMessage", "Successfully registered referral");

        FancyReferrals.getInstance().saveConfig();
    }

    public Database getDatabase(){
        if(dbType == null){
            return null;
        }

        Database db = null;

        switch (dbType){
            case MYSQL -> db = new MySqlDatabase(dbHost, dbPort, dbDatabase, dbUsername, dbPassword);
            case SQLITE -> db = new SqliteDatabase(dbFile);
        }

        return db;
    }

    public int getRefreshTopPlayersInterval() {
        return refreshTopPlayersInterval;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    enum DatabaseType{
        MYSQL("mysql"),
        SQLITE("sqlite");

        private final String identifier;

        DatabaseType(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static DatabaseType getByIdentifier(String identifier){
            for (DatabaseType type : values()) {
                if(type.getIdentifier().equalsIgnoreCase(identifier)){
                    return type;
                }
            }

            return null;
        }
    }

}
