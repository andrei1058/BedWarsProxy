package dev.andrei1058.bedwars.common.api.gui;

import dev.andrei1058.bedwars.common.api.gui.slot.RefreshableSlotHolder;
import dev.andrei1058.bedwars.common.api.gui.slot.SlotHolder;
import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseGUI {

    public static final ItemStack AIR = new ItemStack(Material.AIR);
    @Getter
    private final HashMap<RefreshableSlotHolder, List<Integer>> refreshableSlots = new HashMap<>();

    private final List<String> pattern;
    @Getter
    private Inventory inventory;
    @Getter
    private final LocaleAdapter lang;

    /**
     * Create a GUI from the given pattern.
     * <p>
     * #########
     * ####x####
     * #########
     * <p>
     * Make sure to use {@link BaseGUI#validatePattern(List)} before using it.
     *
     * @param inventoryHolder inventory handler.
     * @param lang         target language.
     * @param pattern         inventory pattern.
     */
    public BaseGUI(@NotNull List<String> pattern, LocaleAdapter lang, @NotNull CustomHolder inventoryHolder, String invName) {
        this.lang = lang;
        this.pattern = pattern;
        inventoryHolder.setGui(this);

        if (pattern.get(0).toCharArray().length == 9) {
            this.inventory = Bukkit.createInventory(inventoryHolder, ((pattern.size() < 7 ? pattern.size() : 1) * 9), ChatColor.translateAlternateColorCodes('&', invName));
        } else if (pattern.get(0).toCharArray().length == 5) {
            this.inventory = Bukkit.createInventory(inventoryHolder, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', invName));
        } else if (pattern.get(0).toCharArray().length == 3) {
            this.inventory = Bukkit.createInventory(inventoryHolder, InventoryType.DISPENSER, ChatColor.translateAlternateColorCodes('&', invName));
        }
    }

    public void withReplacement(char character, SlotHolder slotHolder) {
        List<Integer> slots = getReplacementSlots(character);
        if (slots.isEmpty()) return;

        ItemStack displayItem = slotHolder.getDisplayItem(getLang());
        slots.forEach(slot -> inventory.setItem(slot, displayItem));
    }

    public void withReplacement(char character, RefreshableSlotHolder slotsHolder) {
        List<Integer> slots = getReplacementSlots(character);
        if (slots.isEmpty()) return;

        refreshableSlots.putIfAbsent(slotsHolder, slots);
        refresh();
    }

    protected List<Integer> getReplacementSlots(char symbol) {
        List<Integer> slots = new LinkedList<>();
        final int[] slot = {-1};
        pattern.forEach(row -> {
            char[] chars = row.toCharArray();
            for (char c : chars) {
                slot[0]++;
                if (c == symbol) {
                    slots.add(slot[0]);
                }
            }
        });
        return slots;
    }

    public void refresh() {
        refreshableSlots.forEach((key, value) -> {
            AtomicInteger entry = new AtomicInteger(-1);
            value.forEach(slot -> {
                ItemStack displayItem = key.getSlotItem(entry.incrementAndGet(), getLang(), null);
                inventory.setItem(slot, displayItem == null ? AIR : displayItem);
            });
        });
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public static boolean validatePattern(@NotNull List<String> pattern) {
        if (pattern.isEmpty()) return false;
        if (pattern.get(0).toCharArray().length == 9) {
            // CHEST
            return pattern.stream().noneMatch(string -> string.toCharArray().length != 9) && pattern.size() < 7;
        } else if (pattern.get(0).toCharArray().length == 5) {
            // HOPPER
            return pattern.size() == 1;
        } else if (pattern.get(0).toCharArray().length == 3) {
            // DISPENSER
            return pattern.stream().noneMatch(string -> string.toCharArray().length != 3) && pattern.size() < 4;
        }
        return true;
    }

}
