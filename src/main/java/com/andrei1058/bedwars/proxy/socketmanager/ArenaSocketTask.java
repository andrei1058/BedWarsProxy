package com.andrei1058.bedwars.proxy.socketmanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.*;
import com.andrei1058.bedwars.proxy.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import com.andrei1058.bedwars.proxy.rejoin.RemoteReJoin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class ArenaSocketTask implements Runnable {

    private Socket socket;
    private Scanner scanner;
    private PrintWriter out;

    public ArenaSocketTask(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(socket.toString());
    }

    @Override
    public void run() {
        while (ServerSocketTask.compute && socket.isConnected()) {
            if (scanner.hasNext()) {
                String message = scanner.next();
                if (message.isEmpty()) continue;
                final JsonObject json;
                try {
                    json = new JsonParser().parse(message).getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    BedWarsProxy.getPlugin().getLogger().log(Level.WARNING, "Received bad data from: " + socket.getInetAddress().toString());
                    continue;
                }
                //serverName,remoteIdentifier,arenaName,group,status,maxPlayers,currentPlayers,displayNamePerLanguage
                //OPERATION,data
                if (!json.has("type")) continue;
                switch (json.get("type").getAsString()) {
                    case "RC":
                        CachedArena arena = ArenaManager.getInstance().getArena(json.get("server").getAsString(), json.get("arena_id").getAsString());
                        if (arena == null) continue;
                        RemoteReJoin rrj2 = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                        if (rrj2 != null) rrj2.destroy();
                        new RemoteReJoin(UUID.fromString(json.get("uuid").getAsString()), arena);
                        break;
                    case "RD":
                        RemoteReJoin rrj = RemoteReJoin.getReJoin(UUID.fromString(json.get("uuid").getAsString()));
                        if (rrj == null) continue;
                        if (rrj.getArena().getServer().equals(json.get("server").getAsString())) rrj.destroy();
                        break;
                    case "UPDATE":
                        //update,serverName,remoteIdentifier,arenaName,group,status,maxP,currP,maxInTeam (for parties)
                        CachedArena ca = ArenaManager.getInstance().getArena(json.get("server_name").getAsString(), json.get("arena_identifier").getAsString());
                        if (ca != null) {
                            ca.setLastUpdate(System.currentTimeMillis());
                            boolean modified = false;
                            if (ca.getMaxPlayers() != json.get("arena_max_players").getAsInt()) {
                                ca.setMaxPlayers(json.get("arena_max_players").getAsInt());
                                modified = true;
                            }
                            if (ca.getCurrentPlayers() != json.get("arena_current_players").getAsInt()) {
                                ca.setCurrentPlayers(json.get("arena_current_players").getAsInt());
                                modified = true;
                            }
                            if (ArenaStatus.valueOf(json.get("arena_status").getAsString()) != ca.getStatus()) {
                                ca.setStatus(ArenaStatus.valueOf(json.get("arena_status").getAsString()));
                                modified = true;
                            }
                            /*ca.setArenaGroup(json.get("arena_group").getAsString());
                            ca.setArenaName(json.get("arena_name").getAsString());
                            ca.setMaxInTeam(json.get("arena_max_in_team").getAsInt());*/
                            if (modified) {
                                CachedArena finalCa = ca;
                                Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                                    ArenaManager.getInstance().registerServerSocket(json.get("server_name").getAsString(), this);
                                    ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(finalCa);
                                    Bukkit.getPluginManager().callEvent(e);
                                });
                            }
                            break;
                        }
                        ca = new LegacyArena(json.get("arena_identifier").getAsString(), json.get("server_name").getAsString(), json.get("arena_group").getAsString(), json.get("arena_name").getAsString(),
                                ArenaStatus.valueOf(json.get("arena_status").getAsString()), json.get("arena_max_players").getAsInt(), json.get("arena_current_players").getAsInt(), json.get("arena_max_in_team").getAsInt());
                        CachedArena finalCa = ca;
                        Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), () -> {
                            ArenaManager.getInstance().registerServerSocket(json.get("server_name").getAsString(), this);
                            ArenaManager.getInstance().registerArena(finalCa);
                            ArenaCacheCreateEvent e = new ArenaCacheCreateEvent(finalCa);
                            Bukkit.getPluginManager().callEvent(e);
                        });
                        break;
                    case "Q":
                        TpRequest tr = TpRequest.getTpRequest(UUID.fromString(json.get("requester").getAsString()));
                        if (tr != null && tr.getTarget().equalsIgnoreCase(json.get("name").getAsString())){
                            CachedArena ar = ArenaManager.getInstance().getArena(json.get("server_name").getAsString(),
                                    json.get("arena_id").getAsString());
                            if (ar != null) tr.setArena(ar);
                        }
                        break;
                }
            }
        }
    }

    public PrintWriter getOut() {
        return out;
    }
}
