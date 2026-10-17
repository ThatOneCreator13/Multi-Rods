package com.lincoln.multirods;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HookManager {

    private HookManager() {}

    private static final Map<UUID, FishingHook> HOOKS = new HashMap<>();

    public static void register(UUID rodId, FishingHook hook) {
        HOOKS.put(rodId, hook);
    }

    public static void unregister(UUID rodId) {
        HOOKS.remove(rodId);
    }

    public static FishingHook get(UUID rodId) {
        return HOOKS.get(rodId);
    }

    public static boolean has(UUID rodId) {
        return HOOKS.containsKey(rodId);
    }

    public static void removePlayer(ServerPlayer player) {
        HOOKS.entrySet().removeIf(entry -> {
            FishingHook hook = entry.getValue();
            return hook == null
                    || hook.isRemoved()
                    || hook.getPlayerOwner() == player;
        });
    }
}