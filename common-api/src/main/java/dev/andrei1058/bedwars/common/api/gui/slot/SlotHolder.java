package dev.andrei1058.bedwars.common.api.gui.slot;

import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import org.bukkit.inventory.ItemStack;

public interface SlotHolder {

    ItemStack getDisplayItem(LocaleAdapter lang);
}
