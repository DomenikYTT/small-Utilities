package de.domenik_yt1.small_utilities.neoforge;

import de.domenik_yt1.small_utilities.Small_utilities;
import net.neoforged.fml.common.Mod;

import java.io.IOException;

@Mod(Small_utilities.MOD_ID)
public final class Small_utilitiesNeoForge {
    public Small_utilitiesNeoForge() {
        try {
            Small_utilities.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
