package de.oliver.fancyreferrals;

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

    public void reload(){
        FancyReferrals.getInstance().reloadConfig();
        FileConfiguration config = FancyReferrals.getInstance().getConfig();

        dbType = DatabaseType.getByIdentifier((String) getOrDefault(config, "database.type", "sqlite"));
        if(dbType != null){
            switch (dbType){
                case MYSQL -> {
                    dbHost = (String) getOrDefault(config, "database.host", "localhost");
                    dbPort = (String) getOrDefault(config, "database.port", "3306");
                    dbDatabase = (String) getOrDefault(config, "database.database", "fancyreferrals");
                    dbUsername = (String) getOrDefault(config, "database.username", "root");
                    dbPassword = (String) getOrDefault(config, "database.password", "");
                }

                case SQLITE -> {
                    dbFile = (String) getOrDefault(config, "database.file_path", "database.db");
                    dbFile = "plugins/FancyReferrals/" + dbFile;
                }
            }
        } else {
            FancyReferrals.getInstance().getLogger().warning("Invalid database type provided in config");
        }


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

    public static Object getOrDefault(FileConfiguration config, String path, Object defaultVal){
        if(!config.contains(path)){
            config.set(path, defaultVal);
            return defaultVal;
        }

        return config.get(path);
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
