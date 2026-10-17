package com.lincoln.multirods.client;


import com.lincoln.multirods.MultiRodData;
import com.lincoln.multirods.RodIdManager;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import java.util.UUID;


public record FishingRodCast()
        implements net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty {


    public static final MapCodec<FishingRodCast> MAP_CODEC =
            MapCodec.unit(
                    new FishingRodCast()
            );



    @Override
    public boolean get(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            int seed,
            ItemDisplayContext displayContext
    ) {


        if (!(owner instanceof Player player)) {
            return false;
        }


        UUID rodId =
                RodIdManager.getId(
                        stack
                );


        return MultiRodData.hasHook(
                rodId
        );

    }



    @Override
    public MapCodec<FishingRodCast> type() {

        return MAP_CODEC;

    }

}