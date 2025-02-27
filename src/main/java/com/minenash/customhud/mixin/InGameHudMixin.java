package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.minenash.customhud.CustomHud;
import com.minenash.customhud.data.Crosshairs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1200)
public abstract class InGameHudMixin {

    @Shadow protected abstract void renderCrosshair(MatrixStack matrices);

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void render(MatrixStack matrices, float tickDelta);

    boolean renderAttackIndicator = false;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void renderAttackIndicatorForDebugScreen2(MatrixStack stack, float _tickDelta, CallbackInfo _info) {
        if (CustomHud.getCrosshair() == Crosshairs.DEBUG && MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
            renderAttackIndicator = true;
            renderCrosshair(stack);
            renderAttackIndicator = false;
        }
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    private boolean getDebugCrosshairEnable(boolean original) {
        return client.options.debugEnabled ? original : !renderAttackIndicator && CustomHud.getCrosshair() == Crosshairs.DEBUG;
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0,target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private boolean skipNormalCrosshairRendering(InGameHud hud, MatrixStack stack, int x, int y, int u, int v, int width, int height) {
        return !renderAttackIndicator && CustomHud.getCrosshair() != Crosshairs.NONE;
    }

}
