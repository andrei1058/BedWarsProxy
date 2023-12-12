package dev.andrei1058.bedwars.common.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface CustomHolder extends InventoryHolder {

    void onClick(Player player, ItemStack itemStack, ClickType clickType);

    default void refresh() {
        if (isStatic()) return;
        if (getGui() != null) {
            getGui().refresh();
        }
    }

    /**
     * This means your gui is initialized once and you keep trace of it and refresh it yourself.
     */
    boolean isStatic();

    BaseGUI getGui();

    void setGui(BaseGUI gui);
}

