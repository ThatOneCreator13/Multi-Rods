package com.thatonecreator13.multirods.mixin;

import net.minecraft.world.entity.projectile.FishingHook;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHook.class)
public interface FishingHookAccessor {

    @Accessor("luck")
    int multirods$getLuck();

    @Accessor("lureSpeed")
    int multirods$getLureSpeed();
}