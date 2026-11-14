// PURPOSE: restore saved fishing hooks at their exact saved position

package com.thatonecreator13.multirods;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.UUID;

public class HookRestoreManager {

    public static void restorePlayer(ServerPlayer player) {

        ServerLevel level =
                player.level();

        MultiRodSavedData data =
                MultiRodSavedData.get(level);

        data.getRods().forEach((rodString, rodData) -> {

            if (!rodData.owner().equals(
                    player.getUUID()
            )) {
                return;
            }

            UUID rodId =
                    UUID.fromString(rodString);

            /*
             * If this rod already has a live hook,
             * don't create another one.
             */
            FishingHook existing =
                    MultiRodData.getHook(rodId);

            if (existing != null
                    && !existing.isRemoved()) {

                return;
            }

            /*
             * Only restore the hook in the
             * dimension where it was saved.
             */
            if (!rodData.dimension().equals(
                    level.dimension().toString()
            )) {
                return;
            }

            /*
             * Create the hook using the saved
             * fishing enchantment values.
             */
            FishingHook hook =
                    new FishingHook(
                            player,
                            level,
                            rodData.luck(),
                            rodData.lure()
                    );

            FishingHookAccess access =
                    (FishingHookAccess) hook;

            /*
             * Reconnect the hook to its rod.
             */
            access.multirods$setRodId(
                    rodId
            );

            access.multirods$setActivated(
                    true
            );

            /*
             * Mark it lazy so the restored hook
             * isn't treated like a brand-new cast.
             */
            access.multirods$setLazy(
                    true
            );

            /*
             * Restore the owner/rod relationship.
             */
            MultiRodData.setOwner(
                    rodId,
                    player.getUUID()
            );

            MultiRodData.setHook(
                    rodId,
                    hook
            );

            /*
             * Add the entity to the world first.
             */
            level.addFreshEntity(
                    hook
            );

            /*
             * IMPORTANT:
             *
             * Set the exact saved coordinates AFTER
             * the entity has been added to the world.
             *
             * This prevents the FishingHook constructor
             * or entity spawning process from replacing
             * the saved position.
             */
            hook.setPos(
                    rodData.x(),
                    rodData.y(),
                    rodData.z()
            );

            hook.setDeltaMovement(
                    0.0,
                    0.0,
                    0.0
            );

            /*
             * Make absolutely sure the restored hook
             * is still associated with this rod after
             * being added to the world.
             */
            MultiRodData.setHook(
                    rodId,
                    hook
            );
        });
    }
}