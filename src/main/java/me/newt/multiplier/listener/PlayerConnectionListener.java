package me.newt.multiplier.listener;

import me.newt.multiplier.MultiplierPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    MultiplierPlugin multiplierPlugin;

    public PlayerConnectionListener(MultiplierPlugin multiplierPlugin) {
        this.multiplierPlugin = multiplierPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        multiplierPlugin.getMultiplierAPI().loadMultipliersAsync(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        multiplierPlugin.getMultiplierAPI().removeFromMemory(event.getPlayer().getUniqueId());
    }
}
