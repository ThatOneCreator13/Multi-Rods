package com.thatonecreator13.multirods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LazyHookManager {

    public record LazyHookData(
            UUID owner,
            UUID rodId,
            String dimension,
            double x,
            double y,
            double z,
            int luck,
            int lure
    ) {}

    private static final Map<UUID, LazyHookData> LAZY_HOOKS =
            new HashMap<>();

    public static void makeLazy(LazyHookData data) {

        LAZY_HOOKS.put(
                data.rodId(),
                data
        );

    }

    public static LazyHookData get(UUID rodId) {

        return LAZY_HOOKS.get(rodId);

    }

    public static boolean has(UUID rodId) {

        return LAZY_HOOKS.containsKey(rodId);

    }

    public static void remove(UUID rodId) {

        LAZY_HOOKS.remove(rodId);

    }

}