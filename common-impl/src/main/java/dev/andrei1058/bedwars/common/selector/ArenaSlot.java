package dev.andrei1058.bedwars.common.selector;

import dev.andrei1058.bedwars.common.CommonManager;
import dev.andrei1058.bedwars.common.api.arena.GameStage;
import dev.andrei1058.bedwars.common.api.gui.slot.RefreshableSlotHolder;
import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaSlot implements RefreshableSlotHolder {

    private final List<String> templatesFilter = new ArrayList<>();
    private final List<GameStage> statusFilter = new ArrayList<>();

    public ArenaSlot(@Nullable String templatesFilter, @Nullable String statusFilter) {
        if (templatesFilter != null && !templatesFilter.contains("none") && !templatesFilter.trim().isEmpty()) {
            this.templatesFilter.addAll(Arrays.asList(templatesFilter.trim().split(",")));
        }
        if (statusFilter != null && !statusFilter.contains("none")) {
            for (String parse : statusFilter.trim().split(",")) {
                var filtered = GameStage.getBySlug(parse);
                if (filtered.isEmpty()) {
                    CommonManager.getInstance().getPlugin().getLogger().warning("Invalid game-state filter: " + statusFilter);
                } else {
                    this.statusFilter.add(filtered.get());
                }

            }
        }
    }

    public List<GameStage> getStatusFilter() {
        return statusFilter;
    }

    public List<String> getTemplatesFilter() {
        return templatesFilter;
    }

    @Override
    public ItemStack getSlotItem(int slot, LocaleAdapter lang, @Nullable String template) {
        throw new IllegalStateException("Do not call getSlotItem on ArenaSlot. They're meant for ArenaGUI only.");
    }
}
