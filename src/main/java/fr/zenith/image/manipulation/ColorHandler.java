package fr.zenith.image.manipulation;

import fr.zenith.image.ImageRender;
import org.bukkit.map.MapPalette;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ColorHandler {

    private final byte[] color_cache = new byte[256 * 256 * 256];

    private final ImageRender plugin;

    public ColorHandler(ImageRender plugin) {
        this.plugin = plugin;

        this.initializeCacheAsync();
    }

    private void initializeCacheAsync() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int r = 0; r < 256; r++) {
            int red = r;
            executor.submit(() -> {
                for (int g = 0; g < 256; g++) {
                    for (int b = 0; b < 256; b++) {
                        color_cache[((red * 256 * 256) + (g * 256) + b)] = MapPalette.matchColor(red, g, b);
                    }
                }
            });
        }

        executor.shutdown();
    }

    public byte[] convertImageToMapColors(BufferedImage image, int width, int height) {
        byte[] colors = new byte[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = x < image.getWidth() && y < image.getHeight()
                        ? image.getRGB(x, y)
                        : 0xFFFFFF;
                colors[y * width + x] = color_cache[rgb & 0xFFFFFF];
            }
        }
        return colors;
    }
}
