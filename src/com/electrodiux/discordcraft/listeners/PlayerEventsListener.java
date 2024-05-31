package com.electrodiux.discordcraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.DiscordCraft;
import com.electrodiux.discordcraft.Messages;

public class PlayerEventsListener implements Listener {

    private boolean connectionMessages;
    private boolean playerDeathMessages;

    public PlayerEventsListener() {
        connectionMessages = DiscordCraft.getConfiguration().getBoolean("discord.notifications.connection-messages",
                true);
        playerDeathMessages = DiscordCraft.getConfiguration().getBoolean("discord.notifications.player-death-messages",
                true);

        connectionMessages = true;
        playerDeathMessages = true;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (connectionMessages) {
            Discord.sendGlobalMessage(
                    Messages.getMessage("player.connected", "%player%", event.getPlayer().getName()));
        }
    }

    @EventHandler
    private void onPlayerDisconnect(PlayerQuitEvent event) {
        if (connectionMessages) {
            Discord.sendGlobalMessage(
                    Messages.getMessage("player.disconnected", "%player%", event.getPlayer().getName()));
        }
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        if (playerDeathMessages) {
            String deathMessage = event.getDeathMessage();
            if (deathMessage != null) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();

                    // Bold the player name
                    deathMessage = deathMessage.replace(player.getName(), "**" + player.getName() + "**");

                    Discord.sendGlobalMessage(deathMessage);
                }
            }
        }
    }

}
