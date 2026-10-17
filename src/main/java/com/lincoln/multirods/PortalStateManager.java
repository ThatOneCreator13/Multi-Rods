package com.lincoln.multirods;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PortalStateManager {

    private static final Set<UUID> PORTAL_PLAYERS = new HashSet<>();

    public static void enteringPortal(UUID player) {
        PORTAL_PLAYERS.add(player);
    }

    public static void leavingPortal(UUID player) {
        PORTAL_PLAYERS.remove(player);
    }

    public static boolean isEnteringPortal(UUID player) {
        return PORTAL_PLAYERS.contains(player);
    }
}