// PURPOSE: restore fishing hooks after dimension changes

package com.lincoln.multirods.mixin;

import com.lincoln.multirods.HookRestoreManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {


    @Inject(
            method = "teleport",
            at = @At("RETURN")
    )
    private void multirods$restoreAfterTeleport(
            TeleportTransition transition,
            CallbackInfoReturnable<ServerPlayer> cir
    ) {

        ServerPlayer player =
                (ServerPlayer)(Object)this;


        if (cir.getReturnValue() != null) {

            HookRestoreManager.restorePlayer(player);

        }

    }

}