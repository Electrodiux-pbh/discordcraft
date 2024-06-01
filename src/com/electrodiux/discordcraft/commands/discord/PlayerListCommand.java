package com.electrodiux.discordcraft.commands.discord;

import java.util.List;

import com.electrodiux.discordcraft.PlayerLists;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PlayerListCommand extends DiscordCommand {

    public PlayerListCommand() {
        super("playerlist");

        super.addOption(OptionType.STRING, "list",
                "Selects the type of list this command shows, like 'online', 'whitelisted', 'banned'", false)
                .addChoice("Online", "online")
                .addChoice("Whitelisted", "whitelisted")
                .addChoice("Banned", "banned");
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        OptionMapping listOption = event.getOption("list");
        String listType = listOption == null ? "" : listOption.getAsString().toLowerCase();

        List<String> players = null;

        switch (listType) {
            case "whitelisted":
                players = PlayerLists.getWhitelistedPlayers();
                break;
            case "banned":
                players = PlayerLists.getBannedPlayers();
                break;
            case "online":
            default:
                players = PlayerLists.getOnlinePlayers();
                listType = "online";
                break;
        }

        boolean ephemeral = this.getConfiguration().getBoolean(this.getConfigKey() + "is-ephemeral", false);

        if (players == null || players.isEmpty()) {

            String replyText = "No %list-type% players found!";
            replyText = replyText.replace("%list-type%", listType);

            event.reply(replyText).setEphemeral(ephemeral).queue();

        } else {

            String replyText = "List of %list-type% players (" + players.size() + "):";
            replyText = replyText.replace("%list-type%", listType);

            for (String playerName : players) {
                replyText += "\n- " + playerName;
            }

            event.reply(replyText).setEphemeral(ephemeral).queue();

        }

    }

}
