package dev.andrei1058.bedwars.common.api.gui.slot;

import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import org.bukkit.inventory.ItemStack;

public class StaticSlot implements SlotHolder {

    private final ItemStack item;

    public StaticSlot(ItemStack itemStack){
        this.item = itemStack;
    }

    @Override
    public ItemStack getDisplayItem(LocaleAdapter lang) {
        return item;
    }
}
