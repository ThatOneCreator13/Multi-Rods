// PURPOSE: hook storage and visibility cleanup

package com.thatonecreator13.multirods;

import net.minecraft.world.entity.projectile.FishingHook;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiRodData {

    // PURPOSE: hook storage

    private static final Map<UUID, UUID> OWNERS =
            new HashMap<>();

    private static final Map<UUID, FishingHook> HOOKS =
            new HashMap<>();


    public static void setHook(
            UUID rodId,
            FishingHook hook
    ) {

        HOOKS.put(
                rodId,
                hook
        );

    }


    public static FishingHook getHook(
            UUID rodId
    ) {

        return HOOKS.get(
                rodId
        );

    }


    public static boolean hasHook(
            UUID rodId
    ) {

        FishingHook hook =
                HOOKS.get(
                        rodId
                );

        return hook != null
                && !hook.isRemoved();

    }


    public static void removeHook(
            UUID rodId
    ) {

        HOOKS.remove(
                rodId
        );

        ServerVisibleRodManager.remove(
                rodId
        );

    }


    public static void restoreHook(
            UUID rodId,
            FishingHook hook
    ) {

        HOOKS.put(
                rodId,
                hook
        );

    }


    public static void clearRemoved() {

        HOOKS.entrySet()
                .removeIf(
                        entry ->
                                entry.getValue() == null
                                        || entry.getValue().isRemoved()
                );

    }


    public static void setOwner(
            UUID rodId,
            UUID playerId
    ) {

        OWNERS.put(
                rodId,
                playerId
        );

    }


    public static UUID getOwner(
            UUID rodId
    ) {

        return OWNERS.get(
                rodId
        );

    }

}