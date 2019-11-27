package com.andrei1058.bedwars.proxy.socketmanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import static com.andrei1058.bedwars.proxy.BedWarsProxy.getPlugin;

public class ServerSocketTask {

    private static ServerSocketTask instance = null;

    private ServerSocket serverSocket;
    public static boolean compute = true;
    private int task;

    private ServerSocketTask(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        instance = this;
        compute = true;
        task = Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), ()-> {
            while (compute){
                try {
                    Socket s = serverSocket.accept();
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new ArenaSocketTask(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).getTaskId();
    }

    public static boolean init(int port){
        if (instance == null) {
            try {
                new ServerSocketTask(port);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static void stopTasks(){
        if (instance != null){
            compute = false;
            Bukkit.getScheduler().cancelTask(instance.task);
        }
    }
}
