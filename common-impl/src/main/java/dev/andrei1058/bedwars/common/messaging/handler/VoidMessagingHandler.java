package dev.andrei1058.bedwars.common.messaging.handler;

import dev.andrei1058.bedwars.common.api.messaging.MessagingChannel;
import dev.andrei1058.bedwars.common.api.messaging.MessagingHandler;
import dev.andrei1058.bedwars.common.api.messaging.MessagingPacket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidMessagingHandler implements MessagingHandler {
    @Override
    public boolean registerIncomingPacketChannel(MessagingChannel<?> channel) {
        return false;
    }

    @Override
    public boolean isChannelRegistered(String channel) {
        return false;
    }

    @Override
    public void sendPacket(String channel, MessagingPacket message, boolean async) {

    }

    @Override
    public void sendPackets(String channel, List<MessagingPacket> message, boolean async) {

    }

    @Override
    public @Nullable MessagingChannel<?> getChannelByName(String name) {
        return null;
    }
}

