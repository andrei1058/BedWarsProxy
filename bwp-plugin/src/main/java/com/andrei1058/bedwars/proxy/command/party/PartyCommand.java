package com.andrei1058.bedwars.proxy.command.party;

import com.andrei1058.bedwars.proxy.command.ParentCommand;
import com.andrei1058.bedwars.proxy.api.Messages;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyCommand extends ParentCommand {

    public PartyCommand(String name) {
        super(name);
        addSubCommand(new InviteCMD("invite", ""));
        addSubCommand(new AcceptCMD("accept", ""));
        addSubCommand(new LeaveCMD("leave", ""));
        addSubCommand(new DisbandCMD("disband", ""));
        addSubCommand(new RemoveCMD("remove", ""));
    }

    //owner, target
    private static HashMap<UUID, UUID> partySessionRequest = new HashMap<>();

    @Override
    public void sendDefaultMessage(CommandSender s) {
        if (s instanceof ConsoleCommandSender) return;
        Player p = (Player) s;
        for (String st : LanguageManager.get().getList(p, Messages.COMMAND_PARTY_HELP)) {
            p.sendMessage(st);
        }
    }

    /**
     * Get list of requests.
     */
    public static HashMap<UUID, UUID> getPartySessionRequest() {
        return partySessionRequest;
    }
}
