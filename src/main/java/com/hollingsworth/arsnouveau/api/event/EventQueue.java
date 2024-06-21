package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * For queuing deferred or over-time tasks. Tick refers to the Server or Client Tick event.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
@EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventQueue {
    @NotNull List<ITimedEvent> events = new ArrayList<>();;

    public void tick(boolean serverSide) {
        if (events.isEmpty()) {
            return;
        }

        List<ITimedEvent> stale = new ArrayList<>();
        // Enhanced-for or iterator will cause a concurrent modification.
        for (int i = 0; i < events.size(); i++) {
            ITimedEvent event = events.get(i);
            if (event.isExpired()) {
                stale.add(event);
            } else {
                event.tick(serverSide);
            }
        }
        this.events.removeAll(stale);
    }

    public void addEvent(ITimedEvent event) {
        events.add(event);
    }

    public static EventQueue getServerInstance() {
        if (serverQueue == null)
            serverQueue = new EventQueue();
        return serverQueue;
    }

    public static EventQueue getClientQueue() {
        if (clientQueue == null)
            clientQueue = new EventQueue();
        return clientQueue;
    }


    // Tear down on world unload
    public void clear() {
        for(ITimedEvent event : events){
            event.onServerStopping();
        }
        this.events = new ArrayList<>();
    }

    // Split these because our integrated servers are CURSED and both tick.
    private static EventQueue serverQueue;
    private static EventQueue clientQueue;

    private EventQueue() {
        events = new ArrayList<>();
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post e) {
        EventQueue.getServerInstance().tick(true);
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post e) {
        EventQueue.getClientQueue().tick(false);
    }

}
