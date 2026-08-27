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

@Mixin(targets = "org.ExtraTortik.personalhelperfriend.content.client.render.PersonalHelperFriendMonsterRenderer")
public class PersonalHelperFriendMonsterMeshMixin {

    private static Object loadedBedrockModel = null;
    private static final ResourceLocation TEXTURE_LOC = ResourceLocation.parse("personalhelperfriend:textures/entity/personalhelperfriend_monster.png");

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
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

            // Chỉ cancel render mặc định khi bạn đã có trình render Bedrock thực tế
            if (loadedBedrockModel != null) {
                poseStack.pushPose();
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOC));
                
                // TODO: Gọi hàm vẽ model từ thư viện Bedrock Model hoặc GeckoLib tại đây nếu có
                
                poseStack.popPose();
                // Bỏ comment dòng dưới khi đã gắn hàm vẽ hoàn chỉnh:
                // ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}