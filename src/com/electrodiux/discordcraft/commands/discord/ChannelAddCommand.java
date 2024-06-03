package com.electrodiux.discordcraft.commands.discord;

import com.electrodiux.discordcraft.Discord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ChannelAddCommand extends DiscordCommand {

    public ChannelAddCommand() {
        super("channel-add");

        addOption(
            OptionType.CHANNEL,
            "channel",
            "The channel to add as a linked channel, if no channel is passed it will take the current channel",
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

        if (channel.getGuild().getIdLong() != Discord.getGuild().getIdLong()) {

            String channelNotInServerMessage = "The channel must be in the same server as the bot!";

            event.reply(channelNotInServerMessage).setEphemeral(true).queue();

            return;
        }

        Discord.addLinkedChannel(channel);

        String channelAddedMessage = "Added a new channel %channel%";

        event.reply(channelAddedMessage.replace("%channel%", channel.getAsMention())).setEphemeral(true).queue();
    }

}
