// PURPOSE: 32-block fishing line distance cleanup for active hooks only

package com.thatonecreator13.multirods.mixin;

import com.thatonecreator13.multirods.FishingHookAccess;
import com.thatonecreator13.multirods.MultiRodData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public class FishingHookDistanceMixin {


    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void multirods$distanceCleanup(
            CallbackInfo ci
    ) {
        FishingHook hook =
                (FishingHook) (Object) this;

        if (!(hook.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        if (!(hook instanceof FishingHookAccess access)) {
            return;
        }

        if (access.multirods$getRodId() == null) {
            return;
        }

        /*
         * LAZY HOOKS:
         *
         * A lazy bobber ignores the owner's distance.
         * It must remain frozen indefinitely until another
         * player renders it and wakes it.
         *
         * The normal active 32-block check is handled by
         * MultiRodsFishingHookMixin.
         */
        if (access.multirods$isLazy()) {
            return;
        }

        /*
         * Never apply the distance check across dimensions.
         *
         * The bobber must remain in its original dimension
         * while the owner travels through a portal.
         */
        if (hook.level() != player.level()) {
            return;
        }

        /*
         * ACTIVE HOOK:
         *
         * The normal 32-block line-break check.
         */
        if (hook.distanceToSqr(player) > 1024.0D) {

            MultiRodData.removeHook(
                    access.multirods$getRodId()
            );

            hook.discard();
        }
    }

}
