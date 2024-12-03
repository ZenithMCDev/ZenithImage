package fr.zenith.image.manipulation;

import fr.zenith.image.ImageRender;
import fr.zenith.image.manipulation.defaults.ModifiedClickableImage;
import fr.zenith.image.manipulation.defaults.data.ClickedImageData;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RayTraceHandler {

    private final ImageRender plugin;

    @Getter
    private final Map<Integer, ModifiedClickableImage> clickableImagesCache;

    public RayTraceHandler(ImageRender plugin) {
        this.plugin = plugin;
        this.clickableImagesCache = new HashMap<>();
    }

    public void handlePacket(PacketPlayInUseEntity packet, UUID uniqueId) {
        Player player = plugin.getServer().getPlayer(uniqueId);

        if (player == null) {
            System.out.println("Player is null");
            return;
        }

        int entityId = Arrays.stream(packet.getClass().getDeclaredFields()).filter(field -> field.getType().equals(int.class))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.getInt(packet);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return -1;
                    }
                }).findFirst().orElse(-1);

        if(entityId == -1) {
            return;
        }

        ModifiedClickableImage clickableImage = this.clickableImagesCache.get(entityId);

        if (clickableImage == null) {
            return;
        }

        EntityItemFrame itemFrame = clickableImage.getItemFramesCache().stream().filter(frame -> frame.getId() == entityId).findFirst().orElse(null);

        if(itemFrame == null) {
            return;
        }

        Vec3D impactPoint = this.performRaytrace(player, itemFrame);

        if(impactPoint == null) {
            return;
        }

        Vec3D relativeImpact = this.getRelativeImpact(itemFrame, impactPoint);

        int pixelX = (int) (relativeImpact.a * 128);
        int pixelY = (int) (relativeImpact.b * 128);

        clickableImage.onClick(new ClickedImageData(player, relativeImpact, pixelX, pixelY));
    }

    private Vec3D performRaytrace(Player player, EntityItemFrame frame) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        Vec3D start = new Vec3D(nmsPlayer.locX, nmsPlayer.locY + nmsPlayer.getHeadHeight(), nmsPlayer.locZ);

        float yaw = nmsPlayer.yaw;
        float pitch = nmsPlayer.pitch;
        double dx = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double dy = -Math.sin(Math.toRadians(pitch));
        double dz = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        Vec3D direction = new Vec3D(dx, dy, dz);

        Vec3D end = start.add(direction.a * 5, direction.b * 5, direction.c * 5);

        AxisAlignedBB aabb = frame.getBoundingBox();
        MovingObjectPosition result = aabb.a(start, end);

        return (result != null) ? result.pos : null;
    }

    private Vec3D getRelativeImpact(EntityItemFrame frame, Vec3D impactPoint) {
        Vec3D frameCenter = new Vec3D(frame.locX, frame.locY, frame.locZ);

        double relativeX = impactPoint.a - frameCenter.a;
        double relativeY = impactPoint.b - frameCenter.b;
        double relativeZ = impactPoint.c - frameCenter.c;

        return switch (frame.getDirection()) {
            case NORTH -> new Vec3D(1 - relativeX, relativeY, 0);
            case SOUTH -> new Vec3D(relativeX, relativeY, 1);
            case WEST -> new Vec3D(0, relativeY, 1 - relativeZ);
            case EAST -> new Vec3D(1, relativeY, relativeZ);
            default -> new Vec3D(0, 0, 0);
        };
    }
}
