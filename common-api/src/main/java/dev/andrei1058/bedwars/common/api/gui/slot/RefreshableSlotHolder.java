package dev.andrei1058.bedwars.common.api.gui.slot;

import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface RefreshableSlotHolder {

    @Nullable
    ItemStack getSlotItem(int slot, LocaleAdapter lang, String filter);
}
