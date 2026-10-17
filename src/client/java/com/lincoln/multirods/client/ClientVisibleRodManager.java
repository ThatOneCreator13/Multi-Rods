// PURPOSE: track fishing bobbers rendered by this client

package com.lincoln.multirods.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientVisibleRodManager {

    private static final Set<UUID> VISIBLE_RODS =
            new HashSet<>();

    /*
     * Called at the beginning of a new render frame.
     */
    public static void beginFrame() {

        VISIBLE_RODS.clear();
    }

    /*
     * Called whenever a bobber is actually rendered.
     */
    public static void markVisible(
            UUID rodId
    ) {

        if (rodId != null) {

            VISIBLE_RODS.add(
                    rodId
            );
        }
    }

    /*
     * Used by other client-side MultiRods code.
     */
    public static boolean isVisible(
            UUID rodId
    ) {

        return VISIBLE_RODS.contains(
                rodId
        );
    }

    /*
     * Get a snapshot of the bobbers rendered
     * during the current frame.
     */
    public static Set<UUID> getVisibleRods() {

        return new HashSet<>(
                VISIBLE_RODS
        );
    }
}