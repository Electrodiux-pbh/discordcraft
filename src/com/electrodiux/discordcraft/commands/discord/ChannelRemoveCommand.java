package com.electrodiux.discordcraft.commands.discord;

import com.electrodiux.discordcraft.Discord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ChannelRemoveCommand extends DiscordCommand {

    public ChannelRemoveCommand() {
        super("channel-remove");

        addOption(
            OptionType.CHANNEL,
            "channel",
            "The channel to remove as a linked channel, if no channel is passed it will take the current channel",
            false
        ).setChannelTypes(ChannelType.TEXT);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        TextChannel channel = event.getOption("channel") == null ? event.getChannel().asTextChannel() : event.getOption("channel").getAsChannel().asTextChannel();

        if (channel == null) {
            event.reply("An error occurred while getting the channel!").setEphemeral(true).queue();
            return;
        }

        if (!Discord.isLinkedChannel(channel)) {

            String channelNotWasLinkedMessage = "The channel was not linked!";

            event.reply(channelNotWasLinkedMessage).setEphemeral(true).queue();

            return;
        }

        Discord.removeLinkedChannel(channel);

        String channelAddedMessage = "Removed channel %channel%";

        event.reply(channelAddedMessage.replace("%channel%", channel.getAsMention())).setEphemeral(true).queue();
    }

}
