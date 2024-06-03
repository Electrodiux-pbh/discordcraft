package com.electrodiux.discordcraft.commands.discord;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.LinkedChannel;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ChannelConfigCommand extends DiscordCommand {

    public ChannelConfigCommand() {
        super("channel-config");

        OptionData optionOption = addOption(
            OptionType.STRING,
            "option",
            "The option to configure",
            true
        );

        optionOption.addChoice("Everything", "all");

        optionOption.addChoice("Minecraft Chat", LinkedChannel.MINECRAFT_CHAT_MESSAGES);
        optionOption.addChoice("Player Join", LinkedChannel.PLAYER_JOIN_MESSAGES);
        optionOption.addChoice("Player Leave", LinkedChannel.PLAYER_LEAVE_MESSAGES);
        optionOption.addChoice("Player Death", LinkedChannel.PLAYER_DEATH_MESSAGES);
        optionOption.addChoice("Player Murder", LinkedChannel.PLAYER_MURDER_MENSAGES);

        optionOption.addChoice("Bot Messages", LinkedChannel.DISCORD_BOT_MESSAGES);
        optionOption.addChoice("System Messages", LinkedChannel.DISCORD_SYSTEM_MESSAGES);
        optionOption.addChoice("Discord Chat", LinkedChannel.DISCORD_MESSAGES);

        optionOption.addChoice("Server Start", LinkedChannel.SERVER_START);
        optionOption.addChoice("Server Stop", LinkedChannel.SERVER_STOP);

        OptionData valueOption = addOption(
            OptionType.STRING,
            "value",
            "The value to set the option to",
            true
        );

        valueOption.addChoice("true", "true");
        valueOption.addChoice("false", "false");
        valueOption.addChoice("default", "default");

        addOption(
            OptionType.CHANNEL,
            "channel",
            "The channel to configure, if no channel is passed it will take the current channel",
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

        LinkedChannel linkedChannel = Discord.getLinkedChannel(channel);

        if (linkedChannel == null) {
            event.reply("The channel is not linked!").setEphemeral(true).queue();
            return;
        }

        String option = event.getOption("option").getAsString();

        if (option == null) {
            event.reply("An error occurred while getting the option!").setEphemeral(true).queue();
            return;
        }

        String value = event.getOption("value").getAsString();

        if (value == null) {
            event.reply("An error occurred while getting the value!").setEphemeral(true).queue();
            return;
        }

        boolean computedValue = Boolean.valueOf(value);

        switch (option) {
            case LinkedChannel.MINECRAFT_CHAT_MESSAGES:
                linkedChannel.setSendMinecraftChatMessages(computedValue);
                break;
            case LinkedChannel.PLAYER_JOIN_MESSAGES:
                linkedChannel.setSendPlayerJoinMessages(computedValue);
                break;
            case LinkedChannel.PLAYER_LEAVE_MESSAGES:
                linkedChannel.setSendPlayerLeaveMessages(computedValue);
                break;
            case LinkedChannel.PLAYER_DEATH_MESSAGES:
                linkedChannel.setSendPlayerDeathMessages(computedValue);
                break;
            case LinkedChannel.PLAYER_MURDER_MENSAGES:
                linkedChannel.setSendPlayerMurderMessages(computedValue);
            case LinkedChannel.DISCORD_BOT_MESSAGES:
                linkedChannel.setSendBotMessages(computedValue);
                break;
            case LinkedChannel.DISCORD_SYSTEM_MESSAGES:
                linkedChannel.setSendDiscordSystemMessages(computedValue);
                break;
            case LinkedChannel.DISCORD_MESSAGES:
                linkedChannel.setSendDiscordMessages(computedValue);
                break;
            case LinkedChannel.SERVER_START:
                linkedChannel.setSendServerStartMessages(computedValue);
                break;
            case LinkedChannel.SERVER_STOP:
                linkedChannel.setSendServerStopMessages(computedValue);
                break;
            case "all":
                linkedChannel.setSendMinecraftChatMessages(computedValue);
                linkedChannel.setSendPlayerJoinMessages(computedValue);
                linkedChannel.setSendPlayerLeaveMessages(computedValue);
                linkedChannel.setSendPlayerDeathMessages(computedValue);
                linkedChannel.setSendPlayerMurderMessages(computedValue);
                linkedChannel.setSendBotMessages(computedValue);
                linkedChannel.setSendDiscordSystemMessages(computedValue);
                linkedChannel.setSendDiscordMessages(computedValue);
                linkedChannel.setSendServerStartMessages(computedValue);
                linkedChannel.setSendServerStopMessages(computedValue);
                break;
            default:
                event.reply("Invalid option!").setEphemeral(true).queue();
                return;
        }

        LinkedChannel.saveChannelsConfig(); // Save the configuration

        event.reply("Option " + option + " has been set to " + value + " for channel " + channel.getAsMention()).setEphemeral(true).queue();
    }

}
