// PURPOSE: send client-rendered fishing bobber visibility to the server

package com.lincoln.multirods;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record VisibilityPayload(
        List<UUID> rodIds
) implements CustomPacketPayload {

    public static final Identifier ID =
            MultiRodsMod.id("visible_rods");

    public static final CustomPacketPayload.Type<VisibilityPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ID
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, VisibilityPayload> CODEC =
            StreamCodec.of(
                    VisibilityPayload::write,
                    VisibilityPayload::read
            );

    private static void write(
            RegistryFriendlyByteBuf buf,
            VisibilityPayload payload
    ) {

        buf.writeVarInt(
                payload.rodIds().size()
        );

        for (UUID rodId : payload.rodIds()) {

            buf.writeUUID(
                    rodId
            );
        }
    }

    private static VisibilityPayload read(
            RegistryFriendlyByteBuf buf
    ) {

        int count =
                buf.readVarInt();

        List<UUID> rodIds =
                new ArrayList<>(
                        count
                );

        for (int i = 0; i < count; i++) {

            rodIds.add(
                    buf.readUUID()
            );
        }

        return new VisibilityPayload(
                rodIds
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}