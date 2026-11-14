package com.thatonecreator13.multirods.client.mixin;


import com.thatonecreator13.multirods.MultiRodData;
import com.thatonecreator13.multirods.RodIdManager;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(FishingRodCast.class)
public class FishingRodCastMixin {


    @Inject(
            method = "get",
            at = @At("HEAD"),
            cancellable = true
    )
    private void multirods$changeCastModel(
            ItemStack itemStack, ClientLevel level, LivingEntity owner, int seed, ItemDisplayContext displayContext, CallbackInfoReturnable<Boolean> cir
    ) {


        cir.setReturnValue(
                MultiRodData.hasHook(
                        RodIdManager.getId(itemStack)
                )
        );

    }

}