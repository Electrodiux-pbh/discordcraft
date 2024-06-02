package com.electrodiux.discordcraft.commands.discord;

import com.electrodiux.discordcraft.Discord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SetGlobalChannel extends DiscordCommand {

    public SetGlobalChannel() {
        super("set-global-channel");

        addOption(
            OptionType.CHANNEL,
            "channel",
            "The channel to set as the global channel, if no channel is passed it will take current channel",
            false
        ).setChannelTypes(ChannelType.TEXT);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        if (!this.isEnabled()) {
            event.reply("This command is disabled!").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = event.getOption("channel") == null ? event.getChannel().asTextChannel() : event.getOption("channel").getAsChannel().asTextChannel();

        if (channel == null) {
            event.reply("An error occurred while getting the channel!").setEphemeral(true).queue();
            return;
        }

        Discord.setGlobalChannel(channel);

        event.reply("Global channel set to " + channel.getAsMention()).setEphemeral(true).queue();
        channel.sendMessage("This channel is being set as the global channel!");
    }

}
