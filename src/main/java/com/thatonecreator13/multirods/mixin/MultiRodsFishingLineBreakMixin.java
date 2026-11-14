// PURPOSE: diagnose exact-rod line break behavior

package com.thatonecreator13.multirods.mixin;

import com.thatonecreator13.multirods.FishingHookAccess;
import com.thatonecreator13.multirods.RodIdManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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
public class MultiRodsFishingLineBreakMixin {

    @Unique
    private boolean multirods$isHoldingExactRod(
            ServerPlayer player,
            UUID rodId
    ) {
        ItemStack mainHand =
                player.getItemInHand(
                        InteractionHand.MAIN_HAND
                );

        if (mainHand.is(Items.FISHING_ROD)) {
            UUID mainId = RodIdManager.getId(mainHand);

            if (rodId.equals(mainId)) {
                return true;
            }
        }

        ItemStack offHand =
                player.getItemInHand(
                        InteractionHand.OFF_HAND
                );

        if (offHand.is(Items.FISHING_ROD)) {
            UUID offId = RodIdManager.getId(offHand);

            if (rodId.equals(offId)) {
                return true;
            }
        }

        return false;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void multirods$diagnoseLineBreak(
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
            System.out.println(
                    "[MultiRods LINE BREAK] Hook has NO rod ID"
            );
            return;
        }

        if (!(hook.getOwner()
                instanceof ServerPlayer player)) {

            System.out.println(
                    "[MultiRods LINE BREAK] Owner is NOT ServerPlayer"
            );
            return;
        }

        boolean sameDimension =
                hook.level() == player.level();

        boolean holdingExactRod =
                multirods$isHoldingExactRod(
                        player,
                        rodId
                );

        double distance =
                hook.distanceTo(player);

        System.out.println(
                "[MultiRods LINE BREAK]"
                        + " rodId=" + rodId
                        + " holdingExactRod=" + holdingExactRod
                        + " lazy=" + access.multirods$isLazy()
                        + " sameDimension=" + sameDimension
                        + " distance=" + distance
        );
    }


}
