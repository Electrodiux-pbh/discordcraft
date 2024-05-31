package com.electrodiux.discordcraft.listeners;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.Messages;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class DiscordChatListener extends ListenerAdapter {

    private String messageFormat;
    private String messageEditedFormat;

    public DiscordChatListener() {
        messageFormat = Messages.getMessage("chat.minecraft-format");
        messageEditedFormat = Messages.getMessage("chat.minecraft-edited-format");
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            return;
        }

        onMessage(event, event.getAuthor(), event.getMessage(), messageEditedFormat);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        onMessage(event, event.getAuthor(), event.getMessage(), messageFormat);
    }

    // Message in minecraft chat from discord

    private void onMessage(GenericMessageEvent event, User author, Message message, String format) {
        if (!checkMessage(event, author))
            return;

        // check if message is a global message
        if (Discord.isGlobalMessage(message)) {
            // normal message broadcast

            String messageWithoutAttachments = format
                    .replace("%username%", author.getEffectiveName())
                    .replace("%message%", message.getContentDisplay());

            String[] parts = messageWithoutAttachments.split("%attachments%", 2);

            ComponentBuilder finalMessageBuilder = new ComponentBuilder("");

            if (parts.length > 0) {
                finalMessageBuilder.append(parts[0]);
            }

            if (parts.length >= 2) { // if there is a %attachments% placeholder
                ComponentBuilder attachmentsBuilder = getAttachmentsComponent(message);
                finalMessageBuilder.append(attachmentsBuilder.create());

                finalMessageBuilder.append(parts[1]);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(finalMessageBuilder.create());
            }

            return;
        }
    }

    // Creation of attachments component

    private ComponentBuilder getAttachmentsComponent(Message message) {

        ComponentBuilder attachmentsBuilder = new ComponentBuilder("");

        if (!message.getAttachments().isEmpty()) {

            attachmentsBuilder.append("[");

            List<Attachment> attachmentns = message.getAttachments();
            for (int i = 0; i < attachmentns.size(); i++) {

                Attachment attachment = attachmentns.get(i);

                if (i != 0) {
                    attachmentsBuilder.append(Messages.getMessage("chat.attachment-separator"));
                }

                TextComponent attachmentComponent = new TextComponent(attachment.getFileName());
                attachmentComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()));

                attachmentsBuilder.append(attachmentComponent);

                if (attachment.isSpoiler()) {
                    attachmentsBuilder.append(Messages.getMessage("chat.attachment-spoiler"));
                }

            }

            attachmentsBuilder.append("]");
        }

        return attachmentsBuilder;
    }

    // Check valid message to send

    private boolean checkMessage(GenericMessageEvent event, User author) {
        if (author.isBot() || author.isSystem() || author.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return false;
        }
        return true;
    }

}
