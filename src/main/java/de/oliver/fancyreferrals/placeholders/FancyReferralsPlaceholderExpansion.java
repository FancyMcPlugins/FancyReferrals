package de.oliver.fancyreferrals.placeholders;

import de.oliver.fancyreferrals.FancyReferrals;
import de.oliver.fancyreferrals.ReferralManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyReferralsPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "FancyReferrals";
    }

    @Override
    public @NotNull String getAuthor() {
        return "OliverHD";
    }

    @Override
    public @NotNull String getVersion() {
        return FancyReferrals.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        for (int i = 1; i <= 10; i++) {
            if(params.equalsIgnoreCase("top_" + i + "_name")){
                return ReferralManager.getTopPlayer(i).getKey();
            } else if(params.equalsIgnoreCase("top_" + i + "_count")){
                return String.valueOf(ReferralManager.getTopPlayer(i).getValue());
            }
        }


        return null;
    }
}
