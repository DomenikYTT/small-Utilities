package de.domenik_yt1.small_utilities.config;

import de.domenik_yt1.small_utilities.Small_utilities;
import dev.architectury.platform.Platform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SmallUtilitiesConfig {

    private static int checkIntervalTicks = 20;


    public static int checkIntervalTicks() {
        return checkIntervalTicks;
    }


    public static void load() throws IOException {
        Path file = Platform.getConfigFolder().resolve("small_utilities.yml");
        Properties props = new Properties();

        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file)) {
                props.load(r);

                checkIntervalTicks = Math.max(1, Integer.parseInt(props.getProperty("checkIntervalTicks", "20")));
            } catch (IOException | NumberFormatException e) {
                Small_utilities.LOGGER.error("[Small Utilities] Failed to load config.yml:" + e);
            }
        }

        props.setProperty("checkIntervalTicks", String.valueOf(checkIntervalTicks));
        try (Writer writer = Files.newBufferedWriter(file)) {
            props.store(writer, "Harvester: Interval In Ticks (20 Ticks = 1 Second), must be 1 or higher");
        } catch (IOException e) {
            Small_utilities.LOGGER.error("[Small Utilities] Failed to load config.yml:"+ e);
        }
    }


    private SmallUtilitiesConfig() {}
}
