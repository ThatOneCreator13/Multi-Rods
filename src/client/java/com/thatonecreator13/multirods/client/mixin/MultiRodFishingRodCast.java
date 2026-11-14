package com.thatonecreator13.multirods.client.mixin;


import com.thatonecreator13.multirods.MultiRodData;
import com.thatonecreator13.multirods.RodIdManager;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


import org.jspecify.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(targets = "net.minecraft.client.renderer.item.properties.conditional.FishingRodCast")
public class MultiRodFishingRodCast {


    @Inject(
            method = "get",
            at = @At("HEAD"),
            cancellable = true
    )
    private void multirods$replaceFishingRodCast(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            int seed,
            ItemDisplayContext displayContext,
            CallbackInfoReturnable<Boolean> cir
    ) {


        if (!(owner instanceof Player player)) {
            cir.setReturnValue(false);
            return;
        }


        cir.setReturnValue(
                MultiRodData.hasHook(
                        RodIdManager.getId(stack)
                )
        );
    }
}