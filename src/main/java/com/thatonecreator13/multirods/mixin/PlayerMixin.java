package com.thatonecreator13.multirods.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin {

    /*
     * Intentionally empty.
     *
     * Vanilla only supports one fishing hook per player.
     * Multi Rods manages hooks through CurrentCastManager instead.
     *
     * We must not overwrite player.fishing every tick,
     * otherwise every rod points to the same bobber.
     */
}