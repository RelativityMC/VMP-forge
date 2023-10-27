package com.ishland.vmp.mixins.playerwatching.optimize_nearby_entity_tracking_lookups;

import com.ishland.vmp.common.playerwatching.NearbyEntityTracking;
import com.ishland.vmp.mixins.access.IThreadedAnvilChunkStorageEntityTracker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {

    @Shadow
    @Final
    private Int2ObjectMap<ThreadedAnvilChunkStorage.EntityTracker> entityTrackers;
    @Shadow @Final private ThreadedAnvilChunkStorage.TicketManager ticketManager;
    @Unique
    private final NearbyEntityTracking vmp$nearbyEntityTracking = new NearbyEntityTracking();


    @Redirect(method = "loadEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker;updateTrackedStatus(Ljava/util/List;)V"))
    private void redirectUpdateOnAddEntity(ThreadedAnvilChunkStorage.EntityTracker instance, List<ServerPlayerEntity> players) {
        if (((IThreadedAnvilChunkStorageEntityTracker) instance).getEntity() instanceof ServerPlayerEntity player) {
            this.vmp$nearbyEntityTracking.addPlayer(player);
        }
        this.vmp$nearbyEntityTracking.addEntityTracker(instance);
        // update is done lazily on next tickEntityMovement
    }

    @Redirect(method = "loadEntity", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;values()Lit/unimi/dsi/fastutil/objects/ObjectCollection;"))
    private <T> ObjectCollection<T> nullifyTrackerListOnAddEntity(Int2ObjectMap<T> instance) {
        if (this.entityTrackers == instance) return Int2ObjectMaps.<T>emptyMap().values();
        else return instance.values();
    }

    @Redirect(method = "unloadEntity", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;values()Lit/unimi/dsi/fastutil/objects/ObjectCollection;"))
    private <T> ObjectCollection<T> nullifyTrackerListOnRemoveEntity(Int2ObjectMap<T> instance) {
        if (this.entityTrackers == instance) return Int2ObjectMaps.<T>emptyMap().values();
        else return instance.values();
    }

    @Redirect(method = "unloadEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker;stopTracking()V"))
    private void redirectUpdateOnRemoveEntity(ThreadedAnvilChunkStorage.EntityTracker instance) {
        if (((IThreadedAnvilChunkStorageEntityTracker) instance).getEntity() instanceof ServerPlayerEntity player) {
            this.vmp$nearbyEntityTracking.removePlayer(player);
        }
        this.vmp$nearbyEntityTracking.removeEntityTracker(instance);
        instance.stopTracking();
    }

    @Redirect(method = "updatePosition", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;values()Lit/unimi/dsi/fastutil/objects/ObjectCollection;"))
    private <T> ObjectCollection<T> redirectTrackersOnUpdatePosition(Int2ObjectMap<T> instance, ServerPlayerEntity player) {
        if (this.entityTrackers != instance) {
            return instance.values();
        } else {
            return Int2ObjectMaps.<T>emptyMap().values(); // nullify, already handled in tick call
        }
    }

    /**
     * @author ishland
     * @reason use nearby tracker lookup
     */
    @Overwrite
    public void tickEntityMovement() {
        try {
            this.vmp$nearbyEntityTracking.tick(this.ticketManager);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}