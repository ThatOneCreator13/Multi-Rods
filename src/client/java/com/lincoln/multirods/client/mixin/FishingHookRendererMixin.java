package com.lincoln.multirods.client.mixin;

import com.lincoln.multirods.FishingHookAccess;
import com.lincoln.multirods.client.ClientVisibleRodManager;

import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(FishingHookRenderer.class)
public class FishingHookRendererMixin {


    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void multirods$markVisibleRod(
            FishingHook hook,
            FishingHookRenderState state,
            float partialTicks,
            CallbackInfo ci
    ) {


        if (!(hook instanceof FishingHookAccess access)) {
            return;
        }


        ClientVisibleRodManager.markVisible(
                access.multirods$getRodId()
        );
    }
}