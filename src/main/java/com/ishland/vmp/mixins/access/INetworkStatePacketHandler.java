package com.ishland.vmp.mixins.access;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkState.PacketHandler.class)
public interface INetworkStatePacketHandler {

    @Accessor
    Object2IntMap<Class<? extends Packet<?>>> getPacketIds();
    
}
