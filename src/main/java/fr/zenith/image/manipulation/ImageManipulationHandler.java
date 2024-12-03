package fr.zenith.image.manipulation;

import fr.zenith.image.ImageRender;
import fr.zenith.image.manipulation.defaults.ModifiedClickableImage;
import fr.zenith.image.manipulation.defaults.ModifiedImage;
import fr.zenith.image.manipulation.defaults.data.ClickedImageData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

public class ImageManipulationHandler {

    public static final int MAP_WIDTH = 128;
    public static final int MAP_HEIGHT = 128;

    private final ImageRender plugin;
    private final ColorHandler colorHandler;
    private final Random random = new SecureRandom();

    public ImageManipulationHandler(ImageRender plugin) {
        this.plugin = plugin;
        this.colorHandler = plugin.getColorHandler();
    }

    public ModifiedImage createModifiedImage(BufferedImage image) {
        return new ModifiedImage(plugin, image);
    }

    public ModifiedImage createModifiedImage(int width, int height) {
        return this.createModifiedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    public ModifiedClickableImage createModifiedClickableImage(BufferedImage image, Consumer<ClickedImageData> clickedImageData) {
        return new ModifiedClickableImage(plugin, image, clickedImageData);
    }

    public ModifiedClickableImage createModifiedClickableImage(int width, int height, Consumer<ClickedImageData> clickedImageData) {
        return this.createModifiedClickableImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), clickedImageData);
    }

    public void placeImage(ModifiedImage modifiedImage, Location topLeft, BlockFace facing, Player player) {
        BufferedImage image = modifiedImage.getImage();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        modifiedImage.setLastFacing(facing);
        modifiedImage.setLastLeftTopLocation(topLeft);

        boolean isClickedImage = modifiedImage instanceof ModifiedClickableImage;

        for (int x = 0; x < imageWidth; x += MAP_WIDTH) {
            for (int y = 0; y < imageHeight; y += MAP_HEIGHT) {

                BufferedImage subImage = image.getSubimage(
                        x,
                        y,
                        Math.min(MAP_WIDTH, imageWidth - x),
                        Math.min(MAP_HEIGHT, imageHeight - y)
                );

                int mapId = random.nextInt(Short.MAX_VALUE);

                byte[] mapData = colorHandler.convertImageToMapColors(subImage, MAP_WIDTH, MAP_HEIGHT);

                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

                PacketPlayOutMap packet = new PacketPlayOutMap(mapId, (byte) 0, Collections.emptyList(), mapData, 0, 0, subImage.getWidth(), subImage.getHeight());
                connection.sendPacket(packet);

                String facingName = facing.name();
                Location frameLocation;

                if (facingName.contains("EAST") || facingName.contains("WEST")) {
                    frameLocation = topLeft.clone().add(0, -((double) y / MAP_HEIGHT), -((double) x / MAP_WIDTH));
                } else {
                    frameLocation = topLeft.clone().add(-((double) x / MAP_WIDTH), -((double) y / MAP_HEIGHT), 0);
                }

                EnumDirection facingDirection = this.getFacingDirection(facing);

                EntityItemFrame itemFrame = new EntityItemFrame(((CraftPlayer) player).getHandle().getWorld(),
                        new BlockPosition(frameLocation.getBlockX(), frameLocation.getBlockY(), frameLocation.getBlockZ()),
                        facingDirection);

                itemFrame.setInvisible(true);

                if (isClickedImage) {
                    plugin.getRayTraceHandler().getClickableImagesCache().put(itemFrame.getId(), (ModifiedClickableImage) modifiedImage);
                }

                modifiedImage.getItemFramesCache().add(itemFrame);

                ItemStack mapItem = new ItemStack(org.bukkit.Material.MAP.getId());
                mapItem.setDurability((short) mapId);
                itemFrame.setItem(CraftItemStack.asNMSCopy(mapItem));

                PacketPlayOutSpawnEntity packetSpawn = new PacketPlayOutSpawnEntity(itemFrame, 71, facingDirection.b());
                PacketPlayOutEntityMetadata packetMetadata = new PacketPlayOutEntityMetadata(itemFrame.getId(), itemFrame.getDataWatcher(), true);

                connection.sendPacket(packetSpawn);
                connection.sendPacket(packetMetadata);
            }
        }
    }

    private EnumDirection getFacingDirection(BlockFace facing) {
        return switch (facing) {
            case SOUTH -> EnumDirection.SOUTH;
            case EAST -> EnumDirection.EAST;
            case WEST -> EnumDirection.WEST;
            default -> EnumDirection.NORTH;
        };
    }

    private AxisAlignedBB getAdjustedBoundingBox(EntityItemFrame frame) {
        double width = 0.5;
        double height = 0.5;
        double depth = 0.0625;

        double x = frame.locX;
        double y = frame.locY;
        double z = frame.locZ;

        return switch (frame.getDirection()) {
            case NORTH -> new AxisAlignedBB(
                    x - width / 2, y - height / 2, z - depth,
                    x + width / 2, y + height / 2, z
            );
            case SOUTH -> new AxisAlignedBB(
                    x - width / 2, y - height / 2, z,
                    x + width / 2, y + height / 2, z + depth
            );
            case WEST -> new AxisAlignedBB(
                    x - depth, y - height / 2, z - width / 2,
                    x, y + height / 2, z + width / 2
            );
            case EAST -> new AxisAlignedBB(
                    x, y - height / 2, z - width / 2,
                    x + depth, y + height / 2, z + width / 2
            );
            default -> frame.getBoundingBox();
        };
    }
}
