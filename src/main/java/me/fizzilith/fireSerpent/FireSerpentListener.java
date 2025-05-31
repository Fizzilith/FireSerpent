package me.fizzilith.fireSerpent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class FireSerpentListener implements Listener {
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isCancelled()) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
            if (bPlayer != null && bPlayer.canBend(CoreAbility.getAbility("me.fizzilith.fireSerpent.FireSerpent"))) {
                new me.fizzilith.fireSerpent.FireSerpent(event.getPlayer());
            }

        }
    }
}
