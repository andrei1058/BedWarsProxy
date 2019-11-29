package com.andrei1058.bedwars.proxy.arenamanager;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.configuration.SoundsConfig;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaGUI {

    //Object[0] = inventory, Object[1] = group
    private static HashMap<Player, Object[]> refresh = new HashMap<>();
    private static YamlConfiguration yml = BedWarsProxy.config.getYml();

    //Object[0] = inventory, Object[1] = group
    public static void refreshInv(Player p, Object[] data) {

        List<CachedArena> arenas;
        if (((String)data[1]).equalsIgnoreCase("default")) {
            arenas = new ArrayList<>(ArenaManager.getArenas());
        } else {
            arenas = new ArrayList<>();
            for (CachedArena a : ArenaManager.getArenas()){
                if (a.getArenaGroup().equalsIgnoreCase(data[1].toString())) arenas.add(a);
            }
        }

        arenas = ArenaManager.getSorted(arenas);

        int arenaKey = 0;
        for (String useSlot : BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS).split(",")) {
            int slot;
            try {
                slot = Integer.parseInt(useSlot);
            } catch (Exception e) {
                continue;
            }
            ItemStack i;
            ((Inventory)data[0]).setItem(slot, new ItemStack(Material.AIR));
            if (arenaKey >= arenas.size()) {
                continue;
            }

            CachedArena ca = arenas.get(arenaKey);

            String status;
            switch (ca.getStatus()) {
                case WAITING:
                    status = "waiting";
                    break;
                case PLAYING:
                    status = "playing";
                    break;
                case STARTING:
                    status = "starting";
                    break;
                default:
                    continue;
            }

            i = BedWarsProxy.getItemAdapter().createItem(yml.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", status)),
                    1, (byte) yml.getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", status)));
            if (i == null) i = new ItemStack(Material.BEDROCK);

            if (yml.getBoolean(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", status))) {
                if (i.getItemMeta() != null){
                    ItemMeta im = i.getItemMeta();
                    im.addEnchant(Enchantment.LURE, 1, true);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    i.setItemMeta(im);
                }
            }


            ItemMeta im = i.getItemMeta();
            Language lang = Language.getPlayerLanguage(p);
            if (im != null){
                im.setDisplayName(Language.getMsg(p, Messages.ARENA_GUI_ARENA_CONTENT_NAME).replace("{name}", ca.getDisplayName(lang)));
                List<String> lore = new ArrayList<>();
                for (String s : Language.getList(p, Messages.ARENA_GUI_ARENA_CONTENT_LORE)) {
                    if (!(s.contains("{group}") && ca.getArenaGroup().equalsIgnoreCase("default"))) {
                        lore.add(s.replace("{on}", String.valueOf(ca.getCurrentPlayers())).replace("{max}",
                                String.valueOf(ca.getMaxPlayers())).replace("{status}", ca.getDisplayStatus(lang))
                                .replace("{group}", ca.getDisplayGroup(lang)));
                    }
                }
                im.setLore(lore);
                i.setItemMeta(im);
            }
            i = BedWarsProxy.getItemAdapter().addTag(i, "server", ca.getServer());
            i = BedWarsProxy.getItemAdapter().addTag(i, "world_identifier", ca.getRemoteIdentifier());
            i = BedWarsProxy.getItemAdapter().addTag(i, "cancelClick", "true");

            ((Inventory)data[0]).setItem(slot, i);
            arenaKey++;
        }
    }

    public static void openGui(Player p, String group) {
        int size = BedWarsProxy.config.getYml().getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE);
        if (size % 9 != 0) size = 27;
        if (size > 54) size = 54;
        Inventory inv = Bukkit.createInventory(new SelectorHolder(), size, Language.getMsg(p, Messages.ARENA_GUI_INV_NAME));

        ItemStack i = BedWarsProxy.getItemAdapter().createItem(yml.getString(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "skipped-slot")),
                1, (byte) yml.getInt(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "skipped-slot")));
        if (i == null) i = new ItemStack(Material.BEDROCK);
        i = BedWarsProxy.getItemAdapter().addTag(i, "cancelClick", "true");

        if (i.getItemMeta() != null){
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', BedWarsProxy.config.getString(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP)));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            i.setItemMeta(im);
        }

        for (int x = 0; x < inv.getSize(); x++) {
            inv.setItem(x, i);
        }

        refresh.put(p, new Object[]{inv, group});
        refreshInv(p, new Object[]{inv, group});
        p.openInventory(inv);
        SoundsConfig.playSound("arena-selector-open", p);
    }

    public static HashMap<Player, Object[]> getRefresh() {
        return refresh;
    }

    public static class SelectorHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
