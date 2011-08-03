package to.joe.util.Runnables.BanCooperative;

import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.manager.BanCooperative;
import to.joe.util.BanCooperative.BanCoopDossier;

public class CoopRunnerLookup extends CoopRunner {

    private final Player player;

    public CoopRunnerLookup(J2 j2, BanCooperative coop, String name, Player player) {
        super(j2, coop, name);
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.coop.record.containsKey(this.name)) {
            this.dox();
        }
        final BanCoopDossier dossier = this.coop.record.get(this.name);
        for (final String line : dossier.full()) {
            this.player.sendMessage(line);
        }
    }

}
