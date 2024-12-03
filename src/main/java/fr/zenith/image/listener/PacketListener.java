package fr.zenith.image.listener;

import fr.zenith.image.ImageRender;
import fr.zenith.image.manipulation.RayTraceHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

import java.util.UUID;

public class PacketListener extends ChannelDuplexHandler {

    private final ImageRender plugin;
    private final UUID owner;
    private final RayTraceHandler rayTraceHandler;

    public PacketListener(ImageRender plugin, UUID owner) {
        this.plugin = plugin;
        this.owner = owner;
        this.rayTraceHandler = plugin.getRayTraceHandler();
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, o, channelPromise);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet){
        try {
            if(packet instanceof PacketPlayInUseEntity packetPlayInUseEntity) {
                rayTraceHandler.handlePacket(packetPlayInUseEntity, owner);
            } else {
                super.channelRead(channelHandlerContext, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
