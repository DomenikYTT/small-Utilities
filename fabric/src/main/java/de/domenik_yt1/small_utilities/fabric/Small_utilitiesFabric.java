package de.domenik_yt1.small_utilities.fabric;

import de.domenik_yt1.small_utilities.Small_utilities;
import net.fabricmc.api.ModInitializer;

import java.io.IOException;

public final class Small_utilitiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Small_utilities.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
