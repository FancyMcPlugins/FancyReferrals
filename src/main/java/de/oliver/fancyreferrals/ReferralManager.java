package de.oliver.fancyreferrals;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancylib.databases.Database;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReferralManager {

    private static final Map<UUID, UUID> referrals = new HashMap<>();
    private static List<Map.Entry<String, Integer>> topPlayersCache = new ArrayList<>();

    public static void loadFromDatabase(){
        Database database = FancyReferrals.getInstance().getDatabase();
        ResultSet res = database.executeQuery("SELECT * FROM referrals");
        if(res == null){
            FancyReferrals.getInstance().getLogger().warning("Could not receive data from database");
            return;
        }

        referrals.clear();

        try {
            while (res.next()) {
                String referrerName = res.getString("referrer");
                String referredName = res.getString("referred");

                if(referrerName == null || referrerName.length() == 0 || referredName == null || referredName.length() == 0){
                    continue;
                }

                UUID referrer = UUID.fromString(referrerName);
                UUID referred = UUID.fromString(referredName);
                long timestamp = res.getLong("timestamp"); // TODO: also cache this

                referrals.put(referrer, referred);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return;
        }
    }

    public static void refreshTopPlayerCache(){
        topPlayersCache = new ArrayList<>();

        Database database = FancyReferrals.getInstance().getDatabase();
        ResultSet res = database.executeQuery("SELECT referred, COUNT(*) as count FROM referrals ORDER BY count DESC LIMIT 10");
        if(res == null){
            FancyReferrals.getInstance().getLogger().warning("Could not fetch data");
            return;
        }

        try {
            while (res.next()) {
                String referred = res.getString("referred");
                if(referred == null || referred.length() == 0){
                    continue;
                }

                UUID uuid = UUID.fromString(referred);
                String name = UUIDFetcher.getName(uuid);
                int count = res.getInt("count");

                topPlayersCache.add(Map.entry(name, count));
            }
        }catch (SQLException e){
            FancyReferrals.getInstance().getLogger().warning("Could not fetch data");
            return;
        }


    }

    public static Map.Entry<String, Integer> getTopPlayer(int place){
        place -= 1;

        if(place >= topPlayersCache.size()){
            return Map.entry("N/A", 0);
        }

        return topPlayersCache.get(place);
    }

    public static void refer(Player referrer, String referred){
        if (referrals.containsKey(referrer.getUniqueId())) {
            MessageHelper.error(referrer, "You cannot do this again");
            return;
        }

        if(referrer.getName().equalsIgnoreCase(referred)){
            MessageHelper.error(referrer, "You cannot refer yourself");
            return;
        }

        UUID referredUUID = UUIDFetcher.getUUID(referred);

        if(referredUUID == null){
            MessageHelper.error(referrer, "Could not find player: '" + referred + "'");
            return;
        }

        referrals.put(referrer.getUniqueId(), referredUUID);

        long timestamp = System.currentTimeMillis();

        Database database = FancyReferrals.getInstance().getDatabase();
        boolean success = database.executeNonQuery("INSERT INTO referrals(referrer, referred, timestamp) VALUES('" + referrer.getUniqueId() + "', '" + referredUUID + "', " + timestamp + ")");
        if(!success){
            FancyReferrals.getInstance().getLogger().warning("Could not insert a new referral into database");
            MessageHelper.warning(referrer, "Something went wrong");
            return;
        }
    }

}
