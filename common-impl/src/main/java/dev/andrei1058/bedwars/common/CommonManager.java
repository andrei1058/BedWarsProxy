package dev.andrei1058.bedwars.common;

import dev.andrei1058.bedwars.common.api.CommonProvider;
import dev.andrei1058.bedwars.common.messaging.MessagingCommonManager;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

public class CommonManager {

    @Getter
    private static CommonManager instance;

    @Getter
    private final CommonProvider commonProvider;
    @Getter
    private final Plugin plugin;

    private CommonManager(CommonProvider commonProvider, Plugin plugin) {
        this.commonProvider = commonProvider;
        this.plugin = plugin;
    }

    public static void init(CommonProvider commonProvider, boolean registerMessaging, Plugin plugin) {
        if (instance == null) {
            instance = new CommonManager(commonProvider, plugin);
        }

        if (registerMessaging) {
            MessagingCommonManager.init(plugin);
        }
    }

}
