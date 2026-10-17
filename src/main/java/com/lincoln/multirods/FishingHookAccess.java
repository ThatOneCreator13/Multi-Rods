package com.lincoln.multirods;

import java.util.UUID;

public interface FishingHookAccess {

    void multirods$setRodId(UUID rodId);

    UUID multirods$getRodId();

    boolean multirods$isActivated();

    void multirods$setActivated(boolean activated);

    boolean multirods$isLazy();

    void multirods$setLazy(boolean lazy);
}