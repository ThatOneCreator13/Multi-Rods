// PURPOSE: track which players are currently rendering fishing bobbers

package com.thatonecreator13.multirods;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServerVisibleRodManager {

    /*
     * How long a visibility report remains valid.
     */
    private static final long VISIBILITY_TIMEOUT = 10L;

    /*
     * Player UUID -> (Rod UUID -> last server tick seen)
     */
    private static final Map<UUID, Map<UUID, Long>> PLAYER_VISIBLE =
            new HashMap<>();

    /*
     * Called whenever a client reports the bobbers it
     * rendered during the most recent reporting period.
     */
    public static void updateVisible(
            ServerPlayer player,
            Set<UUID> rodIds
    ) {

        long tick =
                player.level()
                        .getGameTime();

        UUID playerId =
                player.getUUID();

        Map<UUID, Long> visible =
                PLAYER_VISIBLE.computeIfAbsent(
                        playerId,
                        ignored -> new HashMap<>()
                );

        /*
         * Replace the player's current report.
         *
         * This is important because a bobber that stops
         * being rendered must eventually disappear from
         * this player's visibility list.
         */
        visible.clear();

        for (UUID rodId : rodIds) {

            visible.put(
                    rodId,
                    tick
            );
        }
    }

    /*
     * Returns true if ANY player has recently rendered
     * this bobber.
     */
    public static boolean isVisible(
            ServerPlayer player,
            UUID rodId
    ) {

        return isVisibleToOtherPlayer(
                player,
                rodId
        );
    }

    /*
     * Returns true if a player OTHER than the owner
     * has recently rendered this bobber.
     */
    public static boolean isVisibleToOtherPlayer(
            ServerPlayer owner,
            UUID rodId
    ) {

        long currentTick =
                owner.level()
                        .getGameTime();

        UUID ownerId =
                owner.getUUID();

        for (Map.Entry<UUID, Map<UUID, Long>> entry
                : PLAYER_VISIBLE.entrySet()) {

            /*
             * The owner does not count as another player.
             */
            if (entry.getKey().equals(ownerId)) {
                continue;
            }

            Long lastTick =
                    entry.getValue()
                            .get(rodId);

            if (lastTick == null) {
                continue;
            }

            if (currentTick - lastTick
                    <= VISIBILITY_TIMEOUT) {

                return true;
            }
        }

        return false;
    }

    /*
     * Returns true if the specified player has recently
     * rendered this bobber.
     */
    public static boolean isVisibleToPlayer(
            ServerPlayer player,
            UUID rodId
    ) {

        Map<UUID, Long> visible =
                PLAYER_VISIBLE.get(
                        player.getUUID()
                );

        if (visible == null) {
            return false;
        }

        Long lastTick =
                visible.get(
                        rodId
                );

        if (lastTick == null) {
            return false;
        }

        long currentTick =
                player.level()
                        .getGameTime();

        return currentTick - lastTick
                <= VISIBILITY_TIMEOUT;
    }

    /*
     * Remove all visibility records for a player.
     */
    public static void removePlayer(
            UUID playerId
    ) {

        PLAYER_VISIBLE.remove(
                playerId
        );
    }

    /*
     * Remove visibility information when a hook disappears.
     */
    public static void remove(
            UUID rodId
    ) {

        for (Map<UUID, Long> visible
                : PLAYER_VISIBLE.values()) {

            visible.remove(
                    rodId
            );
        }
    }

    /*
     * Remove stale player reports.
     */
    public static void cleanup(
            ServerPlayer player
    ) {

        long currentTick =
                player.level()
                        .getGameTime();

        PLAYER_VISIBLE.entrySet().removeIf(
                entry -> {

                    entry.getValue().entrySet().removeIf(
                            visibleEntry ->
                                    currentTick
                                            - visibleEntry.getValue()
                                            > VISIBILITY_TIMEOUT
                    );

                    return entry.getValue().isEmpty();
                }
        );
    }
}