package com.thatonecreator13.multirods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// PURPOSE: central server-wide storage for fishing bobbers in every dimension

public class MultiRodSavedData extends SavedData {


    /*
     * IMPORTANT:
     *
     * All MultiRods hook data is stored in the Overworld's
     * SavedData storage.
     *
     * RodData.dimension tells us which dimension the actual
     * bobber belongs to.
     */
    public static MultiRodSavedData get(ServerLevel level) {

        ServerLevel overworld =
                level.getServer().getLevel(Level.OVERWORLD);

        if (overworld == null) {
            overworld = level;
        }

        return overworld
                .getDataStorage()
                .computeIfAbsent(TYPE);
    }


    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(
                    MultiRodsMod.MOD_ID,
                    "multi_rods"
            );


    public static final Codec<MultiRodSavedData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                                    Codec.unboundedMap(
                                                    Codec.STRING,
                                                    RodData.CODEC
                                            )
                                            .fieldOf("rods")
                                            .forGetter(
                                                    data -> data.rods
                                            )
                            )
                            .apply(
                                    instance,
                                    MultiRodSavedData::new
                            )
            );


    public static final SavedDataType<MultiRodSavedData> TYPE =
            new SavedDataType<>(
                    ID,
                    MultiRodSavedData::new,
                    CODEC,
                    DataFixTypes.SAVED_DATA_COMMAND_STORAGE
            );


    private final Map<String, RodData> rods;


    public MultiRodSavedData() {

        this.rods = new HashMap<>();

    }


    public MultiRodSavedData(
            Map<String, RodData> rods
    ) {

        this.rods =
                new HashMap<>(rods);

    }


    public void saveRod(
            UUID rodId,
            RodData data
    ) {

        rods.put(
                rodId.toString(),
                data
        );

        setDirty();

    }


    public void removeRod(
            UUID rodId
    ) {

        rods.remove(
                rodId.toString()
        );

        setDirty();

    }


    public Map<String, RodData> getRods() {

        return rods;

    }


    public record RodData(
            UUID owner,
            String dimension,
            double x,
            double y,
            double z,
            int luck,
            int lure
    ) {


        public static final Codec<RodData> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                        Codec.STRING
                                                .xmap(
                                                        UUID::fromString,
                                                        UUID::toString
                                                )
                                                .fieldOf("owner")
                                                .forGetter(
                                                        RodData::owner
                                                ),

                                        Codec.STRING
                                                .fieldOf("dimension")
                                                .forGetter(
                                                        RodData::dimension
                                                ),

                                        Codec.DOUBLE
                                                .fieldOf("x")
                                                .forGetter(
                                                        RodData::x
                                                ),

                                        Codec.DOUBLE
                                                .fieldOf("y")
                                                .forGetter(
                                                        RodData::y
                                                ),

                                        Codec.DOUBLE
                                                .fieldOf("z")
                                                .forGetter(
                                                        RodData::z
                                                ),

                                        Codec.INT
                                                .fieldOf("luck")
                                                .forGetter(
                                                        RodData::luck
                                                ),

                                        Codec.INT
                                                .fieldOf("lure")
                                                .forGetter(
                                                        RodData::lure
                                                )

                                )
                                .apply(
                                        instance,
                                        RodData::new
                                )
                );

    }


}
