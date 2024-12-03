package fr.zenith.image.manipulation.defaults;

import fr.zenith.image.ImageRender;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ModifiedImage {

    private final ImageRender plugin;
    private final BufferedImage image;
    private final Graphics2D graphics;
    private final List<EntityItemFrame> itemFramesCache;
    private final List<Location> itemFramesLocationsCache;

    private Location lastLeftTopLocation;
    private BlockFace lastFacing;

    public ModifiedImage(ImageRender plugin, BufferedImage image) {
        this.plugin = plugin;
        this.image = image;
        this.graphics = image.createGraphics();

        this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        this.itemFramesCache = new ArrayList<>();
        this.itemFramesLocationsCache = new ArrayList<>();

        this.lastLeftTopLocation = null;
        this.lastFacing = null;
    }

    public void drawText(String text, int x, int y, String fontName, Color color, int size) {
        this.graphics.setFont(new Font(fontName, Font.PLAIN, size));
        this.graphics.setColor(color);
        this.graphics.drawString(text, x, y);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color, int thickness, float size) {
        this.graphics.setColor(color);
        this.graphics.setStroke(new BasicStroke(thickness * size));
        this.graphics.drawLine(x1, y1, x2, y2);
    }

    public void drawRectangle(int x, int y, int width, int height, Color color, boolean filled, float size) {
        this.graphics.setColor(color);
        int adjustedWidth = Math.round(width * size);
        int adjustedHeight = Math.round(height * size);

        if (filled) {
            this.graphics.fillRect(x, y, adjustedWidth, adjustedHeight);
        } else {
            this.graphics.drawRect(x, y, adjustedWidth, adjustedHeight);
        }
    }

    public void drawCircle(int x, int y, int radius, Color color, boolean filled, float size) {
        this.graphics.setColor(color);
        int adjustedRadius = Math.round(radius * size);

        if (filled) {
            this.graphics.fillOval(x - adjustedRadius, y - adjustedRadius, adjustedRadius * 2, adjustedRadius * 2);
        } else {
            this.graphics.drawOval(x - adjustedRadius, y - adjustedRadius, adjustedRadius * 2, adjustedRadius * 2);
        }
    }

    public void drawPixel(int x, int y, Color color) {
        this.graphics.setColor(color);
        this.graphics.drawLine(x, y, x, y);
    }

    public void drawImage(BufferedImage image, int x, int y) {
        graphics.drawImage(image, x, y, null);
    }

    public void clear(Color color) {
        this.graphics.setColor(color);
        this.graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    // possibility to send Metadata packet to player instead of destroying and creating new item frames
    public void update(Player player) {
        if (lastLeftTopLocation == null || lastFacing == null) {
            return;
        }

        for (EntityItemFrame entityItemFrame : itemFramesCache) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityItemFrame.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        this.plugin.getImageManipulationHandler().placeImage(this, lastLeftTopLocation, lastFacing, player);
    }
}
