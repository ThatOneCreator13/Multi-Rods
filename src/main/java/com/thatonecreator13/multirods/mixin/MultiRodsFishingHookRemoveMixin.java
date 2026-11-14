// PURPOSE: identify the exact code path that discards a MultiRods fishing bobber

package com.thatonecreator13.multirods.mixin;

import com.thatonecreator13.multirods.FishingHookAccess;
import com.thatonecreator13.multirods.MultiRodData;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public class MultiRodsFishingHookRemoveMixin {


    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void multirods$cleanup(
            Entity.RemovalReason reason,
            CallbackInfo ci
    ) {

        FishingHook hook =
                (FishingHook)(Object)this;

        if (!(hook instanceof FishingHookAccess access)) {
            return;
        }

        if (access.multirods$getRodId() == null) {
            return;
        }

        System.out.println(
                "[MultiRods] BOBBER REMOVED"
                        + " reason=" + reason
                        + " rodId=" + access.multirods$getRodId()
                        + " lazy=" + access.multirods$isLazy()
                        + " x=" + hook.getX()
                        + " y=" + hook.getY()
                        + " z=" + hook.getZ()
        );

        /*
         * Print the Java call stack.
         *
         * This tells us exactly which method called
         * remove()/discard() on the bobber.
         */
        Thread.dumpStack();

        MultiRodData.removeHook(
                access.multirods$getRodId()
        );
    }

}
