package to.joe.util.BanCooperative;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class BanCoopDossier {
    private final EnumMap<BanCoopType, Integer> count;
    private final EnumMap<BanCoopType, ArrayList<BanCoopBan>> bans;
    private final double mcbans_rep;
    private final String name;
    private final int significantCount;

    public BanCoopDossier(String name, EnumMap<BanCoopType, Integer> count, int significantCount, EnumMap<BanCoopType, ArrayList<BanCoopBan>> bans, double mcbans_rep) {
        this.count = count;
        this.bans = bans;
        this.mcbans_rep = mcbans_rep;
        this.name = name;
        this.significantCount = significantCount;
    }

    public String oneLiner() {
        return ChatColor.GREEN + this.name + ChatColor.AQUA + " has " + ChatColor.GREEN + this.totalBans() + ChatColor.AQUA + " bans. MCBans rep " + ChatColor.GREEN + this.mcbans_rep + ChatColor.AQUA + "/10";
    }

    public int totalBans() {
        int total = 0;
        for (final Integer i : this.count.values()) {
            total += i.intValue();
        }
        return total;
    }

    public int sigBans() {
        return this.significantCount;
    }

    public double getMCBansRep() {
        return this.mcbans_rep;
    }

    public ArrayList<String> full() {
        final ArrayList<String> result = new ArrayList<String>();
        result.add(this.oneLiner());
        for (final Map.Entry<BanCoopType, ArrayList<BanCoopBan>> ban : this.bans.entrySet()) {
            int total = this.count.get(ban.getKey());
            if (total > 3) {
                total = 3;
            }
            final ArrayList<BanCoopBan> typeBans = ban.getValue();
            for (int x = 0; x < total; x++) {
                result.add(typeBans.get(x).toString());
            }
        }
        return result;
    }
}
