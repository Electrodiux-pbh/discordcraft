package com.electrodiux.discordcraft.commands.discord;

import java.util.ArrayList;
import java.util.List;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandManager extends ListenerAdapter {

    private List<DiscordCommand> commands = new ArrayList<>();

    public CommandManager() {

        try {
            // Add commands here
            commands.add(new PlayerListCommand());
            commands.add(new StopServerCommand());
            commands.add(new SetGlobalChannel());
        } catch (Exception e) {
            DiscordCraft.getInstance().getLogger().severe("An error occurred while adding commands!");
            e.printStackTrace();
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        for (DiscordCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command.isEnabled()) {

                    if (command.isAdministratorOnly()) {
                        // Check if the user has the required permissions
                        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                            event.reply("You don't have the required permissions to use this command!")
                                    .setEphemeral(true).queue();
                            return;
                        }
                    }

                    // Catch any exceptions that occur while executing the command
                    try {
                        // Execute the command
                        command.onCommandInteraction(event);
                    } catch (Exception e) {
                        DiscordCraft.getInstance().getLogger()
                                .severe("An error occurred while executing command: " + command.getName());
                        e.printStackTrace();
                    }

                    return;
                } else {
                    // Command is disabled
                    event.reply("This command is disabled!").setEphemeral(true).queue();
                    return;
                }
            }
        }

        // Command not found
        event.reply("Command not found!").setEphemeral(true).queue();
    }

    private void registerCommands(Guild guild) {
        // Register commands

        DiscordCraft.getInstance().getLogger().info("Registering commands for guild " + guild.getName());

        if (guild.getIdLong() == DiscordCraft.getConfiguration().getLong("discord.bot.server")) {

            DiscordCraft.getInstance().getLogger()
                    .info("Guild matches main server, registering commands. (" + commands.size() + " commands)");

            // Register commands

            List<CommandData> commandDataList = new ArrayList<>();

            for (DiscordCommand command : commands) {

                DiscordCraft.getInstance().getLogger()
                        .info("Registering command: " + command.getName() + " is enabled: "
                                + command.isEnabled());

                if (command.isEnabled()) {
                    SlashCommandData data = Commands.slash(command.getName(), command.getDescription());

                    if (command.hasOptions()) {
                        data.addOptions(command.getOptions());
                    }

                    commandDataList.add(data);
                }
            }

            guild.updateCommands().addCommands(commandDataList).queue();
        }

    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerCommands(event.getGuild());
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        registerCommands(event.getGuild());
    }

    public void addCommands(DiscordCommand... commands) {
        for (DiscordCommand command : commands) {
            this.commands.add(command);
        }
    }

    public void addCommands(List<DiscordCommand> commands) {
        this.commands.addAll(commands);
    }

}