// PURPOSE: store rod identity, persistence, and protect bobbers during portal travel

package com.thatonecreator13.multirods.mixin;

import com.thatonecreator13.multirods.FishingHookAccess;
import com.thatonecreator13.multirods.MultiRodData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FishingHook.class)
public class FishingHookMixin implements FishingHookAccess {


    @Unique
    private UUID multirods$rodId;

    @Unique
    private boolean multirods$activated;

    @Unique
    private boolean multirods$lazy;

    @Unique
    private FishingHook multirods$getHook() {
        return (FishingHook) (Object) this;
    }

    @Override
    public void multirods$setRodId(UUID rodId) {
        this.multirods$rodId = rodId;
    }

    @Override
    public UUID multirods$getRodId() {
        return multirods$rodId;
    }

    @Override
    public boolean multirods$isActivated() {
        return multirods$activated;
    }

    @Override
    public void multirods$setActivated(boolean activated) {
        this.multirods$activated = activated;
    }

    @Override
    public boolean multirods$isLazy() {
        return multirods$lazy;
    }

    @Override
    public void multirods$setLazy(boolean lazy) {
        this.multirods$lazy = lazy;
    }

    @Inject(
            method = "shouldStopFishing",
            at = @At("HEAD"),
            cancellable = true
    )
    private void multirods$protectHook(
            Player owner,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (multirods$rodId == null) {
            return;
        }

        FishingHook hook =
                multirods$getHook();

        /*
         * LAZY HOOKS
         *
         * A lazy bobber must remain in its original
         * dimension and position indefinitely.
         *
         * Vanilla fishing cleanup must not remove it.
         */
        if (multirods$lazy) {
            cir.setReturnValue(false);
            return;
        }

        /*
         * PORTAL PROTECTION
         *
         * If the owner is in another dimension, the bobber
         * must remain alive in its original dimension.
         *
         * Vanilla shouldStopFishing() must therefore not
         * be allowed to remove it.
         */
        if (hook.level() != owner.level()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void multirods$saveData(
            ValueOutput output,
            CallbackInfo ci
    ) {
        if (multirods$rodId == null) {
            return;
        }

        output.putString(
                "MultiRodsRodId",
                multirods$rodId.toString()
        );

        output.putBoolean(
                "MultiRodsActivated",
                multirods$activated
        );

        output.putBoolean(
                "MultiRodsLazy",
                multirods$lazy
        );
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void multirods$loadData(
            ValueInput input,
            CallbackInfo ci
    ) {
        input.getString(
                "MultiRodsRodId"
        ).ifPresent(
                value -> {
                    try {
                        multirods$rodId =
                                UUID.fromString(value);
                    } catch (IllegalArgumentException ignored) {
                        multirods$rodId = null;
                    }
                }
        );

        multirods$activated =
                input.getBooleanOr(
                        "MultiRodsActivated",
                        false
                );

        multirods$lazy =
                input.getBooleanOr(
                        "MultiRodsLazy",
                        false
                );
    }

    @Inject(
            method = "setOwner",
            at = @At("TAIL")
    )
    private void multirods$setOwner(
            Entity owner,
            CallbackInfo ci
    ) {
        if (multirods$rodId == null) {
            return;
        }

        if (owner instanceof ServerPlayer player) {
            MultiRodData.setOwner(
                    multirods$rodId,
                    player.getUUID()
            );
        }
    }

    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void multirods$removeData(
            Entity.RemovalReason reason,
            CallbackInfo ci
    ) {
        /*
         * Do not remove saved Multi-Rods data here.
         *
         * Portal travel and lazy-state restoration may need
         * this bobber to remain registered.
         */
    }


}
