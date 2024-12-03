package fr.zenith.image.listener;

import fr.zenith.image.ImageRender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {

    private static final String CHANNEL_NAME = "custom_handler";

    private final ImageRender plugin;

    public BukkitListener(ImageRender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        ((CraftPlayer) player).getHandle()
                .playerConnection
                .networkManager
                .channel
                .pipeline()
                .addBefore("packet_handler", CHANNEL_NAME,
                        new PacketListener(plugin, player.getUniqueId()));

    }
}
