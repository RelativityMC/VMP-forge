package com.ishland.vmp.mixins.chunk.loading.async_chunk_on_player_login;

import com.ishland.vmp.common.chunk.loading.async_chunks_on_player_login.IAsyncChunkPlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements IAsyncChunkPlayer {

    @Unique
    private NbtCompound vmp$playerData = null;

    @Unique
    private boolean vmp$chunkLoadCompleted = true;

    @Override
    public void markPlayerForAsyncChunkLoad() {
        this.vmp$chunkLoadCompleted = false;
    }

    @Override
    public void setPlayerData(NbtCompound nbtCompound) {
        this.vmp$playerData = nbtCompound;
    }

    @Override
    public NbtCompound getPlayerData() {
        return this.vmp$playerData;
    }

    @Override
    public boolean isChunkLoadCompleted() {
        return this.vmp$chunkLoadCompleted;
    }

    @Override
    public void onChunkLoadComplete() {
        this.vmp$chunkLoadCompleted = true;
    }

    @Inject(
            method = {
                    "playerTick"
            },
            at = @At("HEAD"),
            cancellable = true
    )
    private void suppressActionsDuringChunkLoad(CallbackInfo ci) {
        if (!this.vmp$chunkLoadCompleted) ci.cancel();
    }
}