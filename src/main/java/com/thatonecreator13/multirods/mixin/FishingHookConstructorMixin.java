package com.thatonecreator13.multirods.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.projectile.FishingHook;

/*
 * Disabled.
 *
 * FishingRodItemMixin now assigns the rod UUID directly
 * after creating each FishingHook.
 *
 * Vanilla FishingHook construction assumes one hook per player,
 * so modifying constructor behavior here causes different rods
 * to overwrite each other.
 */
@Mixin(FishingHook.class)
public class FishingHookConstructorMixin {

}