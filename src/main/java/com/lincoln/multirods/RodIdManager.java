package com.lincoln.multirods;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class RodIdManager {

    private static final String ROD_ID_KEY = "MultiRodsRodId";


    public static UUID getId(ItemStack stack) {

        CustomData customData =
                stack.get(DataComponents.CUSTOM_DATA);


        if (customData != null) {

            CompoundTag tag =
                    customData.copyTag();


            if (tag.contains(ROD_ID_KEY)) {

                String value =
                        tag.getString(ROD_ID_KEY).orElse("");


                try {

                    return UUID.fromString(value);

                } catch (IllegalArgumentException ignored) {

                    // Bad old data, replace it.
                }
            }
        }


        UUID newId =
                UUID.randomUUID();


        CompoundTag newTag =
                new CompoundTag();


        if (customData != null) {
            newTag = customData.copyTag();
        }


        newTag.putString(
                ROD_ID_KEY,
                newId.toString()
        );


        stack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(newTag)
        );


        return newId;
    }
}