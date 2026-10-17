// PURPOSE: give each fishing rod its own independent fishing hook and protect cross-dimension hooks

package com.lincoln.multirods.mixin;

import com.lincoln.multirods.FishingHookAccess;
import com.lincoln.multirods.MultiRodData;
import com.lincoln.multirods.MultiRodSavedData;
import com.lincoln.multirods.RodIdManager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin {


    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    private void multirods$use(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {

        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel =
                (ServerLevel) level;

        ItemStack rod =
                player.getItemInHand(hand);

        UUID rodId =
                RodIdManager.getId(rod);

        /*
         * Try to find this rod's existing hook.
         */
        FishingHook hook =
                MultiRodData.getHook(rodId);

        if (hook != null && !hook.isRemoved()) {

            /*
             * CROSS-DIMENSION PROTECTION
             *
             * This rod's bobber belongs to another dimension.
             *
             * Do NOT retrieve it.
             * Do NOT discard it.
             * Do NOT create another bobber.
             *
             * The original bobber must remain exactly where
             * it is in its original dimension.
             */
            if (hook.level() != level) {

                cir.setReturnValue(
                        InteractionResult.SUCCESS
                );

                return;
            }

            /*
             * Normal retrieval of this rod's own bobber.
             */
            int damage =
                    hook.retrieve(rod);

            rod.hurtAndBreak(
                    damage,
                    player,
                    hand.asEquipmentSlot()
            );

            if (hook instanceof FishingHookAccess access) {

                access.multirods$setActivated(
                        false
                );

                access.multirods$setLazy(
                        false
                );
            }

            MultiRodData.removeHook(
                    rodId
            );

            MultiRodSavedData.get(serverLevel)
                    .removeRod(rodId);

            cir.setReturnValue(
                    InteractionResult.SUCCESS
            );

            return;
        }

        /*
         * If MultiRods still knows about a hook,
         * don't create another one.
         */
        if (MultiRodData.hasHook(rodId)) {

            cir.setReturnValue(
                    InteractionResult.SUCCESS
            );

            return;
        }

        /*
         * Calculate the fishing enchantment values.
         */
        int lure =
                (int) EnchantmentHelper.getFishingTimeReduction(
                        serverLevel,
                        rod,
                        player
                );

        int luck =
                EnchantmentHelper.getFishingLuckBonus(
                        serverLevel,
                        rod,
                        player
                );

        /*
         * Create the new hook.
         */
        FishingHook newHook =
                new FishingHook(
                        player,
                        level,
                        luck,
                        lure
                );

        FishingHookAccess access =
                (FishingHookAccess) newHook;

        access.multirods$setRodId(
                rodId
        );

        access.multirods$setActivated(
                true
        );

        access.multirods$setLazy(
                false
        );

        /*
         * Register the hook with this rod.
         */
        MultiRodData.setOwner(
                rodId,
                player.getUUID()
        );

        MultiRodData.setHook(
                rodId,
                newHook
        );

        /*
         * Add the hook to the world.
         */
        level.addFreshEntity(
                newHook
        );

        /*
         * Save the rod data.
         *
         * FishingHookUnloadMixin will update the saved
         * coordinates with the hook's real position.
         */
        cir.setReturnValue(
                InteractionResult.SUCCESS
        );
    }


}
