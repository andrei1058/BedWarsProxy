package dev.andrei1058.bedwars.proxy.selector;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public class GameSelectorListener implements Listener {

    //todo
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerGameJoin(@NotNull PlayerGameJoinEvent e) {
//        if (!e.isCancelled()) {
//            GameConnector.newChain().delay(10).sync(() -> SelectorManager.getINSTANCE().refreshArenaSelector()).execute();
//        }
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onGameStateChange(GameStateChangeEvent e) {
//        GameConnector.newChain().delay(10).sync(
//                () -> SelectorManager.getINSTANCE().refreshArenaSelector()
//        ).execute();
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onGameDrop(@NotNull GameDropEvent e) {
//        ArenaManager.getInstance().remove(e.getArena());
//    }
}
