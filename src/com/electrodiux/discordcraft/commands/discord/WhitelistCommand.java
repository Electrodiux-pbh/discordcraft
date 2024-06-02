package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WhitelistCommand extends DiscordCommand {

    public WhitelistCommand() {
        super("whitelist");

        boolean allowToggleWhitelist = getConfig().getBoolean("allow-toggle-whitelist", true);
        boolean allowModifyWhitelist = getConfig().getBoolean("allow-modify-whitelist", true);

        OptionData actionOption = addOption(OptionType.STRING, "action", "The reason for the whitelist", true);

        if (allowToggleWhitelist) {
            actionOption.addChoice("Enable", "enable");
            actionOption.addChoice("Disable", "disable");
        }

        if (allowModifyWhitelist) {
            actionOption.addChoice("Add", "add");
            actionOption.addChoice("Remove", "remove");

            addOption(OptionType.STRING, "player", "The player to whitelist", false);
        }

        if (!allowToggleWhitelist && !allowModifyWhitelist) {
            setEnabled(false);
        }
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String player = event.getOption("player") == null ? null : event.getOption("player").getAsString();
        String action = event.getOption("action").getAsString();
        
        switch (action) {
            case "enable":

                String enableMessage = getConfig().getString("messages.enable", "The whitelist has been enabled!");
                String alreadyEnableMessage = getConfig().getString("messages.already-enable", "The whitelist is already enabled!");

                if (Bukkit.hasWhitelist()) {
                    event.reply(alreadyEnableMessage).setEphemeral(isEphemeral).queue();
                    return;
                }

                Bukkit.setWhitelist(true);

                event.reply(enableMessage).setEphemeral(isEphemeral).queue();
                return;
            case "disable":

                String disableMessage = getConfig().getString("messages.disable", "The whitelist has been disabled!");
                String alreadyDisableMessage = getConfig().getString("messages.already-disable", "The whitelist is already disabled!");

                if (!Bukkit.hasWhitelist()) {
                    event.reply(alreadyDisableMessage).setEphemeral(isEphemeral).queue();
                    return;
                }

                Bukkit.setWhitelist(false);

                event.reply(disableMessage).setEphemeral(isEphemeral).queue();
                return;
        }
        
        String notFoundMessage = getConfig().getString("messages.not-found", "User %player% not found!");
        String noPlayerOption = getConfig().getString("messages.no-player-option", "You must specify a player!");
        
        if (player == null) {
            event.reply(noPlayerOption).setEphemeral(isEphemeral).queue();
            return;
        }

        // Get offline player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        
        if (offlinePlayer.getUniqueId() == null) {
            event.reply(notFoundMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
            return;
        }

        switch (action) {
            case "add":

                String addSuccessMessage = getConfig().getString("messages.add-success", "The player %player% has been added to the whitelist!");
                String alreadyWhitelistedMessage = getConfig().getString("messages.already-whitelisted", "The player %player% is already whitelisted!");

                if (offlinePlayer.isWhitelisted()) {
                    event.reply(alreadyWhitelistedMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
                    return;
                }

                offlinePlayer.setWhitelisted(true);
                event.reply(addSuccessMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();

                break;
            case "remove":

                String removeSuccessMessage = getConfig().getString("messages.remove-success", "The player %player% has been removed from the whitelist!");
                String notWhitelistedMessage = getConfig().getString("messages.not-whitelisted", "The player %player% is not whitelisted!");

                if (!offlinePlayer.isWhitelisted()) {
                    event.reply(notWhitelistedMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
                    return;
                }

                offlinePlayer.setWhitelisted(false);
                event.reply(removeSuccessMessage.replace("%player%", player)).setEphemeral(isEphemeral).queue();
                
                break;
        }

    }
}