package com.lincoln.multirods.client.mixin;

import com.lincoln.multirods.client.FishingHookRenderStateAccess;

import net.minecraft.client.renderer.entity.state.FishingHookRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;


@Mixin(FishingHookRenderState.class)
public class FishingHookRenderStateMixin implements FishingHookRenderStateAccess {


    @Unique
    private UUID multirods$rodId;

    @Override
    public void multirods$setRodId(UUID id) {
        this.multirods$rodId = id;
    }


    @Override
    public UUID multirods$getRodId() {
        return this.multirods$rodId;
    }
}