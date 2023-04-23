package de.oliver.fancyreferrals;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.databases.Database;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReferralManager {

    private static final Map<UUID, UUID> referrals = new HashMap<>();

    public static void loadFromDatabase(){
        Database database = FancyReferrals.getInstance().getDatabase();
        ResultSet res = database.executeQuery("SELECT * FROM referrals");
        if(res == null){
            FancyReferrals.getInstance().getLogger().warning("Could not receive data from database");
            return;
        }

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

    public static void refer(Player referrer, Player referred){
        if (referrals.containsKey(referrer.getUniqueId())) {
            MessageHelper.error(referrer, "You cannot do this again");
            return;
        }

        if(referrer.getName().equals(referred.getName())){
            MessageHelper.error(referrer, "You cannot refer yourself");
            return;
        }

        referrals.put(referrer.getUniqueId(), referred.getUniqueId());

        long timestamp = System.currentTimeMillis();

        Database database = FancyReferrals.getInstance().getDatabase();
        boolean success = database.executeNonQuery("INSERT INTO referrals(referrer, referred, timestamp) VALUES('" + referrer.getUniqueId() + "', '" + referred.getUniqueId() + "', " + timestamp + ")");
        if(!success){
            FancyReferrals.getInstance().getLogger().warning("Could not insert a new referral into database");
            MessageHelper.warning(referrer, "Something went wrong");
            return;
        }
    }

}
