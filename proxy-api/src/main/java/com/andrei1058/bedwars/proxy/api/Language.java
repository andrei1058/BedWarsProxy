package com.andrei1058.bedwars.proxy.api;

import java.util.List;

@Deprecated
public interface Language {

    /**
     * Set chat prefix.
     *
     * @param prefix messages prefix.
     */
    void setPrefix(String prefix);

    /**
     * Get language iso code.
     *
     * @return language iso code. Ex: ro, en.
     */
    String getIso();

    /**
     * Get language name.
     * This is used in the /bw lang list.
     * It is a sort of display name.
     *
     * @return language display name.
     */
    String getLangName();

    /**
     * Check if a message was set.
     *
     * @param path message path.
     * @return true if the given path exists.
     */
    boolean exists(String path);

    /**
     * Get a color translated message.
     *
     * @param path message path.
     * @return a color translated message.
     */
    String getMsg(String path);

    /**
     * Get a color translated list.
     *
     * @param path message path.
     * @return a color translated list.
     */
    List<String> getList(String path);

    /**
     * Set a message.
     *
     * @param path  message path.
     * @param value message value.
     */
    void set(String path, Object value);
}
