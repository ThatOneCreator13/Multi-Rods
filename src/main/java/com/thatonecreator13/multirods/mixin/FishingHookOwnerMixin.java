// PURPOSE: vanilla ownership control

package com.thatonecreator13.multirods.mixin;

import com.thatonecreator13.multirods.FishingHookAccess;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(FishingHook.class)
public class FishingHookOwnerMixin {


    /**
     * PURPOSE:
     * Only allow the active MultiRods hook
     * to become player.fishing.
     *
     * Vanilla only supports one hook.
     * MultiRods keeps the others independent.
     */

    @Overwrite
    private void updateOwnerInfo(FishingHook hook) {


        FishingHook self =
                (FishingHook)(Object)this;


        Player owner =
                self.getPlayerOwner();


        if (owner == null) {
            return;
        }



        if (self instanceof FishingHookAccess access) {


            /*
             * Only activated hooks behave
             * like vanilla hooks.
             */

            if (access.multirods$isActivated()) {

                owner.fishing = hook;

            }

            return;
        }

        /*
         * Vanilla fallback.
         */

        owner.fishing = hook;

    }
}