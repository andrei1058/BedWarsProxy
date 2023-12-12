package dev.andrei1058.bedwars.common.api.selector;

import dev.andrei1058.bedwars.common.api.arena.GameStage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum GameSelectorItemEnum {

    SELECTOR_WAITING("waiting", "STAINED_CLAY","CONCRETE", "GREEN_GLAZED_TERRACOTTA", (byte) 5, false),
    SELECTOR_STARTING("starting", "STAINED_CLAY","CONCRETE", "YELLOW_GLAZED_TERRACOTTA", (byte) 4, false),
    SELECTOR_PLAYING("playing", "STAINED_CLAY","CONCRETE", "ORANGE_GLAZED_TERRACOTTA", (byte) 1, false),
    SELECTOR_ENDING("ending", "STAINED_CLAY","CONCRETE", "RED_GLAZED_TERRACOTTA", (byte) 14, false);


    public static final String MAIN_PATH = "game-selector-display-item";
    public final byte SERVER_VERSION = Byte.parseByte(Bukkit.getServer().getClass().getName().split("\\.")[3].split("_")[1]);

    private final String path;
    @Getter
    private final String material;
    @Getter
    private final short data;
    @Getter
    private final boolean enchanted;

    GameSelectorItemEnum(String path,String mat_8, String mat_12, String mat_13, short data, boolean enchanted) {
        this.path = MAIN_PATH + "." + path;
        this.material = SERVER_VERSION == 8 ? mat_8 : SERVER_VERSION >= 13 ? mat_13 : mat_12;
        this.data = data;
        this.enchanted = enchanted;
    }

    /**
     * Export default values to a yml configuration.
     *
     * @param yaml config.
     */
    @SuppressWarnings("unused")
    public void export(@NotNull YamlConfiguration yaml) {
        yaml.addDefault(materialPath(), material);
        yaml.addDefault(dataPath(), data);
        yaml.addDefault(enchantedPath(), enchanted);
    }

    @Contract(pure = true)
    public @NotNull String materialPath() {
        return path + ".material";
    }

    @Contract(pure = true)
    public @NotNull String dataPath() {
        return path + ".data";
    }

    @Contract(pure = true)
    public @NotNull String enchantedPath() {
        return path + ".enchanted";
    }

    @Contract(pure = true)
    public static GameSelectorItemEnum getForState(@NotNull GameStage state) {
        return switch (state) {
            case WAITING -> SELECTOR_WAITING;
            case STARTING -> SELECTOR_STARTING;
            case IN_GAME -> SELECTOR_PLAYING;
            default -> SELECTOR_ENDING;
        };
    }

//    public static CommonMessage getNameForState(GameStage state) {
//        switch (state) {
//            case WAITING:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_WAITING_NAME;
//            case STARTING:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_STARTING_NAME;
//            case IN_GAME:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_PLAYING_NAME;
//            default:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_ENDING_NAME;
//        }
//    }

//    public static CommonMessage getLoreForState(GameState state) {
//        switch (state) {
//            case WAITING:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_WAITING_LORE;
//            case STARTING:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_STARTING_LORE;
//            case IN_GAME:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_PLAYING_LORE;
//            default:
//                return CommonMessage.SELECTOR_DISPLAY_ITEM_ENDING_LORE;
//        }
//    }
}
