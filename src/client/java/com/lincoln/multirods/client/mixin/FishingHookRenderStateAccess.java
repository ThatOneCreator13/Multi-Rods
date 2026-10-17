package com.lincoln.multirods.client;

import java.util.UUID;

public interface FishingHookRenderStateAccess {

    void multirods$setRodId(UUID id);

    UUID multirods$getRodId();

}