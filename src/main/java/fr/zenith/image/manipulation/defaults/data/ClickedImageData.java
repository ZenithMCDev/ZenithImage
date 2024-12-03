package fr.zenith.image.manipulation.defaults.data;

import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record ClickedImageData(Player player, Vec3D relativeImpact, int x, int y) {

    Location getLocation() {
        return player.getLocation();
    }

    public int getPixelX() {
        return x;
    }

    public int getPixelY() {
        return y;
    }


}
