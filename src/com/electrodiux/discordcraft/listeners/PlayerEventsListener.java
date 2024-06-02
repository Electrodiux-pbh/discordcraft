package com.electrodiux.discordcraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                String deathMessageFormat = Messages.getRawMessage("player.death");

                EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
                String deathMessage = Messages.getRawMessage("custom-death-messages." + damageEvent.getCause().name().toLowerCase());

                if (deathMessage == null) {
                    deathMessage = event.getDeathMessage();
                } else {
                    deathMessage = deathMessage.replace("%player%", player.getName());
                }

                deathMessage = deathMessage.replace(player.getName(), "**" + player.getName() + "**"); // Bold the player name

                Discord.sendGlobalMessage(
                    // Replace the placeholders in the death message format
                    deathMessageFormat
                    .replace("%player%", player.getName())
                    .replace("%death_message%",  deathMessage)
                );
            }
        }
    }

}
