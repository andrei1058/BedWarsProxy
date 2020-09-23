package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.api.CachedArena;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.UUID;

public class TpRequest {

    private UUID requester;
    private String target;
    private CachedArena arena = null;
    private long millis;

    private static LinkedList<TpRequest> requests = new LinkedList<>();

    public TpRequest(UUID requester, String target){
        this.requester = requester;
        this.target = target;
        requests.add(this);

        JsonObject jo = new JsonObject();
        jo.addProperty("type", "Q");
        jo.addProperty("name", target);
        jo.addProperty("requester", requester.toString());

        for (ArenaSocketTask ast : ArenaManager.getInstance().getSocketByServer().values()) {
            ast.getOut().println(jo.toString());
        }
            this.millis = System.currentTimeMillis()+3000;
    }

    public static TpRequest getTpRequest(UUID requester){
        for (TpRequest tr : requests){
            if (tr.requester.equals(requester)){
                if (tr.millis > System.currentTimeMillis()){
                    return tr;
                } else {
                    requests.remove(tr);
                    return null;
                }
            }
        }
        return null;
    }

    public CachedArena getArena() {
        return arena;
    }

    public String getTarget() {
        return target;
    }

    public void setArena(CachedArena arena) {
        this.arena = arena;
    }
}
