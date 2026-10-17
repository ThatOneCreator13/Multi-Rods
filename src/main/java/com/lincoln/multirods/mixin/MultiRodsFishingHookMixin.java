// PURPOSE: manage lazy fishing hooks, nearby-player wake-up, portal protection, and line breaks

package com.lincoln.multirods.mixin;

import com.lincoln.multirods.FishingHookAccess;
import com.lincoln.multirods.MultiRodData;
import com.lincoln.multirods.RodIdManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(FishingHook.class)
public class MultiRodsFishingHookMixin {

    @Unique
    private boolean multirods$isOwnerHoldingRod(
            ServerPlayer player,
            UUID rodId
    ) {
        ItemStack mainHand =
                player.getItemInHand(
                        InteractionHand.MAIN_HAND
                );

        if (mainHand.is(Items.FISHING_ROD)
                && rodId.equals(
                RodIdManager.getId(mainHand)
        )) {
            return true;
        }

        ItemStack offHand =
                player.getItemInHand(
                        InteractionHand.OFF_HAND
                );

        return offHand.is(Items.FISHING_ROD)
                && rodId.equals(
                RodIdManager.getId(offHand)
        );
    }

    @Unique
    private boolean multirods$isPlayerNearby(
            FishingHook hook
    ) {
        double wakeDistanceSqr = 1024.0D;

        for (Player player :
                hook.level().players()) {

            if (player.distanceToSqr(hook)
                    <= wakeDistanceSqr) {

                return true;
            }
        }

        return false;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void multirods$manageLazyHook(
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
                instanceof ServerPlayer owner)) {
            return;
        }

        /*
         * FIRST CHECK:
         *
         * Is ANY player within 32 blocks of the bobber?
         *
         * This includes the owner.
         *
         * If someone is nearby, wake the bobber immediately.
         *
         * This happens before the dimension, lazy, rod-holding,
         * and distance checks.
         */
        if (hook.level() == owner.level()
                && multirods$isPlayerNearby(hook)) {

            access.multirods$setLazy(false);
        }

        /*
         * CROSS-DIMENSION PROTECTION
         *
         * The bobber remains in its original dimension.
         *
         * If the owner is somewhere else, the bobber becomes
         * lazy and stays completely frozen.
         */
        if (hook.level() != owner.level()) {

            access.multirods$setLazy(true);

            hook.setDeltaMovement(
                    0.0,
                    0.0,
                    0.0
            );

            return;
        }

        /*
         * LAZY MODE
         *
         * If nobody is nearby, remain lazy and frozen.
         *
         * No rod-holding or distance checks happen while lazy.
         */
        if (access.multirods$isLazy()) {

            hook.setDeltaMovement(
                    0.0,
                    0.0,
                    0.0
            );

            return;
        }

        /*
         * ACTIVE HOOK
         *
         * Lazy mode has been disabled, so all normal checks
         * work again.
         */

        /*
         * EXACT ROD CHECK
         *
         * If the owner is not holding this exact rod,
         * break the line.
         */
        if (!multirods$isOwnerHoldingRod(
                owner,
                rodId
        )) {

            MultiRodData.removeHook(
                    rodId
            );

            hook.discard();

            return;
        }

        /*
         * 32-BLOCK DISTANCE CHECK
         *
         * If the owner is more than 32 blocks away,
         * enter lazy mode.
         */
        if (hook.distanceToSqr(
                owner
        ) > 1024.0D) {

            access.multirods$setLazy(true);

            hook.setDeltaMovement(
                    0.0,
                    0.0,
                    0.0
            );

            return;
        }

        /*
         * ACTIVE AND WITHIN RANGE.
         */
        access.multirods$setLazy(false);
    }


}
