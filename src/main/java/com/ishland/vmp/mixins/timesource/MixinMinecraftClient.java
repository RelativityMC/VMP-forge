package com.ishland.vmp.mixins.timesource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

//    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onWindowFocusChanged(Z)V"))
//    private void afterTimeSourceChange(CallbackInfo ci) {
//        Util.nanoTimeSupplier = System::nanoTime;
//    }

}
