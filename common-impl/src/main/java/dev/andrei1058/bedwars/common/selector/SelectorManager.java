package dev.andrei1058.bedwars.common.selector;

import dev.andrei1058.bedwars.common.api.locale.LocaleAdapter;
import dev.andrei1058.bedwars.common.selector.config.SelectorConfig;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class SelectorManager {

    public static final String NBT_P_CMD_KEY = "pl-cmd-1058";
    public static final String NBT_C_CMD_KEY = "co-cmd-1058";

    @Getter
    private static SelectorManager INSTANCE;
    @Getter
    private SelectorConfig selectorConfig;
    private final File selectorDirectory;

    // a gui per language, gui name, gui instance
    private final HashMap<LocaleAdapter, HashMap<String, ArenaGUI>> arenaGUIs = new HashMap<>();

    private SelectorManager(Plugin plugin, File selectorDirectory) {
        this.selectorDirectory = selectorDirectory;
    }

    public void openToPlayer(Player player, LocaleAdapter lang, String guiName) {
        // fixme
        HashMap<String, ArenaGUI> selectors = arenaGUIs.entrySet().stream()
                .filter(es -> es.getKey().getCode().equals(lang.getCode())).findFirst().get().getValue();
        if (selectors == null) return;
        ArenaGUI selector = selectors.get(guiName);
        if (selector == null) return;
        selector.open(player);
    }

    /**
     * To be used when an arena changes
     */
    public void refreshArenaSelector() {
        arenaGUIs.forEach((lang, map) -> map.forEach((name, gui) -> gui.refresh()));
    }

    // this is initialized in common manager
    public static void init(Plugin plugin, String selectorDirectory) {
        if (INSTANCE == null) {

            File selectorDir = plugin.getDataFolder();
            // change directory eventually
            if (!selectorDirectory.isEmpty()) {
                File newPath = new File(selectorDirectory);
                if (newPath.isDirectory()) {
                    selectorDir = newPath;
                    plugin.getLogger().info("Set selector configuration path to: " + selectorDirectory);
                } else {
                    plugin.getLogger().warning("Tried to set selector configuration path to: " + selectorDirectory + " but it does not seem like a directory.");
                }
            }
            INSTANCE = new SelectorManager(plugin, selectorDir);

            // save or load config
            INSTANCE.selectorConfig = new SelectorConfig(plugin, "layout_selector");

            // load selector guis
            for (String guiName : getINSTANCE().getSelectorConfig().getYml().getConfigurationSection("").getKeys(false)) {
                if (guiName == null) continue;
                if (guiName.isEmpty()) continue;
//                if (BaseGUI.validatePattern(getINSTANCE().getSelectorConfig().getList(guiName + "." + SelectorConfig.SELECTOR_GENERIC_PATTERN_PATH))) {
//                    CommonManager.getInstance().getCommonProvider().getCommonLocaleManager().getEnabledCommonLocales().forEach(lang -> {
//                        HashMap<String, ArenaGUI> map = getINSTANCE().arenaGUIs.get(lang);
//                        if (map == null) {
//                            map = new HashMap<>();
//                        }
//                        map.putIfAbsent(guiName, new ArenaGUI(guiName, getINSTANCE().getSelectorConfig().getList(guiName + "." + SelectorConfig.SELECTOR_GENERIC_PATTERN_PATH), lang));
//                        if (getINSTANCE().arenaGUIs.containsKey(lang)) {
//                            getINSTANCE().arenaGUIs.replace(lang, map);
//                        } else {
//                            getINSTANCE().arenaGUIs.put(lang, map);
//                        }
//                    });
//                } else {
//                    plugin.getLogger().warning("Could not validate selector pattern: " + guiName);
//                }
                HashMap<String, ArenaGUI> map = new HashMap<>();
                map.put(guiName, new ArenaGUI(guiName, getINSTANCE().getSelectorConfig()
                        .getList(guiName + "." + SelectorConfig.SELECTOR_GENERIC_PATTERN_PATH), null));
                getINSTANCE().arenaGUIs.put(new LocaleAdapter() {
                    @Override
                    public String getCode() {
                        return "en";
                    }

                    @Override
                    public @Nullable String getPlayerLocale(UUID player) {
                        return null;
                    }

                    @Override
                    public void savePlayerLocale(UUID player, @Nullable String iso) {

                    }
                }, map);
            }

            // append selector sub command to the main command
//            SelectorCommand.append(CommonManager.getSingleton().getCommonProvider().getMainCommand());
        }
    }

    public File getSelectorDirectory() {
        return selectorDirectory;
    }
}
