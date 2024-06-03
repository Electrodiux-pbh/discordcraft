package com.electrodiux.discordcraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.LinkedChannel;
import com.electrodiux.discordcraft.Messages;

public class MinecraftChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String messageFormat = Messages.getMessage("chat.discord-format");

        String messageToSend = messageFormat.replace("%player%", event.getPlayer().getName()) .replace("%message%", event.getMessage());
        
        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendMinecraftChatMessages()) {
                linkedChannel.getChannel().sendMessage(messageToSend).queue();
            }
        }
    }

}
