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
                UUID referrer = UUID.fromString(res.getString("referrer"));
                UUID referred = UUID.fromString(res.getString("referred"));
                long timestamp = res.getLong("timestamp"); // TODO: also cache this

                referrals.put(referrer, referred);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return;
        }
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
