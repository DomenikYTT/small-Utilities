package de.domenik_yt1.small_utilities;

import de.domenik_yt1.small_utilities.config.SmallUtilitiesConfig;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class Small_utilities {
    public static final String MOD_ID = "small_utilities";

    public static final Logger LOGGER = LogManager.getLogger();


    public static void init() throws IOException {
        SmallUtilitiesConfig.load();
        SURegistry.BLOCKS.register();
        SURegistry.ITEMS.register();
        SURegistry.BLOCK_ENTITIES.register();
        CreativeTabRegistry.append(CreativeModeTabs.FUNCTIONAL_BLOCKS, SURegistry.HARVESTER_ITEM);
    }
}
