package com.khangtan959.personalhelperfriend.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.InputStream;
import java.util.Optional;

@Mixin(targets = "org.ExtraTortik.personalhelperfriend.content.client.render.PersonalHelperFriendMonsterRenderer", remap = false)
public class PersonalHelperFriendMonsterMeshMixin {

    private static Object loadedBedrockModel = null;
    private static final ResourceLocation TEXTURE_LOC = ResourceLocation.parse("personalhelperfriend:textures/entity/personalhelperfriend_monster.png");

    @Inject(
        method = "render", 
        at = @At("HEAD"), 
        cancellable = true, 
        remap = false
    )
    private void injectVerityJsonModel(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        try {
            if (loadedBedrockModel == null) {
                ResourceLocation modelLoc = ResourceLocation.parse("personalhelperfriend:models/verity_monster.geo.json");
                ResourceLocation animLoc = ResourceLocation.parse("personalhelperfriend:animations/verity_monster.animation.json");
                
                var rm = Minecraft.getInstance().getResourceManager();
                Optional<Resource> modelRes = rm.getResource(modelLoc);
                Optional<Resource> animRes = rm.getResource(animLoc);
                
                if (modelRes.isPresent() && animRes.isPresent()) {
                    try (InputStream modelStream = modelRes.get().open();
                         InputStream animStream = animRes.get().open()) {
                        if (modelStream != null && animStream != null) {
                            loadedBedrockModel = new Object();
                        }
                    }
                }
            }

            if (loadedBedrockModel != null) {
                poseStack.pushPose();
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOC));
                
                // Mã render mô hình Bedrock của bạn ở đây
                
                poseStack.popPose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}