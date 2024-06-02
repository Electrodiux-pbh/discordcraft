package com.electrodiux.discordcraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.Messages;

public class MinecraftChatListener implements Listener {

    private String messageFormat;

    public MinecraftChatListener() {
        messageFormat = Messages.getMessage("chat.discord-format");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String messageToSend = messageFormat.replace("%player%", event.getPlayer().getName())
                .replace("%message%", event.getMessage());
        Discord.sendGlobalMessage(messageToSend);
    }

}
