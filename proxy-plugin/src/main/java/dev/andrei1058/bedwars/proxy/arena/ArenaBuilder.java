package dev.andrei1058.bedwars.proxy.arena;

import dev.andrei1058.bedwars.common.api.messaging.ISlaveServer;
import dev.andrei1058.bedwars.common.api.messaging.packet.PostGameDataPacket;
import dev.andrei1058.bedwars.proxy.api.arena.ProxiedGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ArenaBuilder {
    private ArenaBuilder(){}

    @Contract("_, _ -> new")
    public static @NotNull ProxiedGame buildFromPacket(ISlaveServer slave, @NotNull PostGameDataPacket packet){
        var arena =  new ProxiedArena(
                slave, packet.getGameId(), packet.getTemplateWorld(), packet.getDisplayName(), packet.getGameState(),
                packet.getSpectatePermission(), packet.getMaxPlayers(), packet.getMinPlayers(), packet.getCurrentPlayers(),
                packet.getCurrentSpectators(), packet.getVips(), null, packet.getGameGroup()
        );
        arena.setPrivateGame(Boolean.TRUE.equals(packet.isPrivateGame()));
        if (null != packet.getDisplayMaterial()){
            // todo
//            ItemStack item = ItemUtil.createItem(
//                    packet.getDisplayMaterial(),
//                    packet.getDisplayData(), 1,
//                    packet.isDisplayEnchantment(),
//                    null
//            );
            var item = new ItemStack(Material.ACACIA_FENCE);
            arena.setDisplayItem(item);
        }

        return arena;
    }
}
