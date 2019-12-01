package com.andrei1058.bedwars.proxy.socketmanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaStatus;
import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import com.andrei1058.bedwars.proxy.arenamanager.LegacyArena;
import com.andrei1058.bedwars.proxy.event.ArenaCacheCreateEvent;
import com.andrei1058.bedwars.proxy.event.ArenaCacheUpdateEvent;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getPlugin;

public class ArenaSocketTask implements Runnable {

    private Socket socket;
    private Scanner scanner;
    private PrintWriter out;

    public ArenaSocketTask(Socket socket){
        this.socket = socket;
        try {
            socket.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
        while (ServerSocketTask.compute && socket.isConnected()){
            if (scanner.hasNext()){
                String message = scanner.next();
                if (message.isEmpty()) continue;
                //serverName,remoteIdentifier,arenaName,group,status,maxPlayers,currentPlayers,displayNamePerLanguage
                //OPERATION,data
                String[] data = message.split(",");
                if (data.length <= 1) continue;
                switch (data[0]){
                    case "alive":
                        CachedArena ca2 = ArenaManager.getInstance().getArena(data[1], data[2]);
                        ca2.setLastUpdate(System.currentTimeMillis());
                        break;
                    case "d_name":
                        //d_name,serverName,arenaName,iso,name
                        break;
                    case "update":
                        if (data.length != 9) break;
                        //update,serverName,remoteIdentifier,arenaName,group,status,maxP,currP,maxInTeam (for parties)
                        CachedArena ca = ArenaManager.getInstance().getArena(data[1], data[2]);
                        int max, current, maxInTeam;
                        ArenaStatus status;
                        try {
                            max = Integer.parseInt(data[6]);
                            current = Integer.parseInt(data[7]);
                            status = ArenaStatus.valueOf(data[5].toUpperCase());
                            maxInTeam = Integer.parseInt(data[8]);
                        } catch (Exception ex){
                            getPlugin().getLogger().log(Level.WARNING, "Received bad data from: " + socket.toString());
                            break;
                        }
                        if (ca != null){
                            ca.setMaxPlayers(max);
                            ca.setCurrentPlayers(current);
                            ca.setStatus(status);
                            ca.setArenaGroup(data[4]);
                            ca.setArenaName(data[3]);
                            ca.setMaxInTeam(maxInTeam);
                            CachedArena finalCa = ca;
                            Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), ()-> {
                                ArenaManager.getInstance().registerServerSocket(data[1], this);
                                ArenaCacheUpdateEvent e = new ArenaCacheUpdateEvent(finalCa);
                                Bukkit.getPluginManager().callEvent(e);
                            });
                            break;
                        }
                        ca = new LegacyArena(data[2], data[1],data[4], data[3], status, max, current, maxInTeam);
                        CachedArena finalCa = ca;
                        Bukkit.getScheduler().runTask(BedWarsProxy.getPlugin(), ()->{
                            ArenaManager.getInstance().registerServerSocket(data[1], this);
                            ArenaManager.getInstance().registerArena(finalCa);
                            ArenaCacheCreateEvent e = new ArenaCacheCreateEvent(finalCa);
                            Bukkit.getPluginManager().callEvent(e);
                        });
                        break;
                }
            }
        }
    }

    public PrintWriter getOut() {
        return out;
    }
}
