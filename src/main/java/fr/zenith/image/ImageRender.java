package fr.zenith.image;

import fr.zenith.image.listener.BukkitListener;
import fr.zenith.image.manipulation.ColorHandler;
import fr.zenith.image.manipulation.ImageManipulationHandler;
import fr.zenith.image.manipulation.RayTraceHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ImageRender extends JavaPlugin {

    public static ImageRender instance;

    private ColorHandler colorHandler;
    private ImageManipulationHandler imageManipulationHandler;
    private RayTraceHandler rayTraceHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.colorHandler = new ColorHandler(this);
        this.imageManipulationHandler = new ImageManipulationHandler(this);
        this.rayTraceHandler = new RayTraceHandler(this);

        this.getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        this.getLogger().info("Plugin enabled");
    }
}
