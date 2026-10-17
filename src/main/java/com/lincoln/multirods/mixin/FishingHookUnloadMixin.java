// PURPOSE: save exact bobber position without interfering with lazy-state management

package com.lincoln.multirods.mixin;

import com.lincoln.multirods.FishingHookAccess;
import com.lincoln.multirods.MultiRodSavedData;
import com.lincoln.multirods.ServerVisibleRodManager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(FishingHook.class)
public class FishingHookUnloadMixin {


    @Unique
    private boolean multirods$hasBeenVisible = false;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void multirods$saveAndFreeze(
            CallbackInfo ci
    ) {
        FishingHook hook =
                (FishingHook) (Object) this;

        if (!(hook instanceof FishingHookAccess access)) {
            return;
        }

        UUID rodId =
                access.multirods$getRodId();

        if (rodId == null) {
            return;
        }

        if (!(hook.getOwner()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!(hook.level()
                instanceof ServerLevel serverLevel)) {
            return;
        }

        /*
         * Visibility is only used to remember that the bobber
         * has been seen before.
         *
         * This mixin does NOT wake lazy hooks.
         *
         * MultiRodsFishingHookMixin is the only place that
         * is allowed to wake a lazy bobber.
         */
        boolean visible =
                ServerVisibleRodManager.isVisible(
                        player,
                        rodId
                );

        if (visible) {
            multirods$hasBeenVisible = true;
        }

        /*
         * Once this bobber has been seen and is no longer
         * visible to another player, preserve lazy mode.
         *
         * Do not turn lazy mode off here.
         */
        if (multirods$hasBeenVisible
                && !visible
                && access.multirods$isLazy()) {

            hook.setDeltaMovement(
                    0.0,
                    0.0,
                    0.0
            );
        }

        /*
         * Save the bobber's exact position and dimension.
         */
        MultiRodSavedData.get(serverLevel)
                .saveRod(
                        rodId,
                        new MultiRodSavedData.RodData(
                                player.getUUID(),
                                serverLevel.dimension().toString(),
                                hook.getX(),
                                hook.getY(),
                                hook.getZ(),
                                ((FishingHookAccessor) hook)
                                        .multirods$getLuck(),
                                ((FishingHookAccessor) hook)
                                        .multirods$getLureSpeed()
                        )
                );
    }


}
