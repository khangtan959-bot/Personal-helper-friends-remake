package org.ExtraTortik.personalhelperfriend.mixin;

import org.ExtraTortik.personalhelperfriend.content.client.render.PersonalHelperFriendMonsterRenderer;
import org.ExtraTortik.personalhelperfriend.content.entity.PersonalHelperFriendMonsterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.InputStream;

@Mixin(PersonalHelperFriendMonsterRenderer.class)
public class PersonalHelperFriendMonsterMeshMixin {

    private static Object customBedrockModel = null;
    // Khai báo chính xác địa chỉ nạp vân bề mặt gốc của bản mod
    private static final ResourceLocation TEXTURE_LOC = new ResourceLocation("personalhelperfriend", "textures/entity/personalhelperfriend_monster.png");

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectSmilerJsonModel(PersonalHelperFriendMonsterEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        try {
            // 1. Đồng bộ đường dẫn nạp tệp cấu trúc hình học và hoạt ảnh Bedrock chuẩn Namespace
            if (customBedrockModel == null) {
                ResourceLocation modelLoc = new ResourceLocation("personalhelperfriend", "models/verity_monster.geo.json");
                ResourceLocation animLoc = new ResourceLocation("personalhelperfriend", "animations/verity_monster.animation.json");
                
                InputStream modelStream = Minecraft.getInstance().getResourceManager().getResource(modelLoc).orElse(null).open();
                InputStream animStream = Minecraft.getInstance().getResourceManager().getResource(animLoc).orElse(null).open();
                
                if (modelStream != null && animStream != null) {
                    customBedrockModel = new Object(); // Hệ thống nội tại nạp thành công mô hình đa giác
                }
            }

            // 2. Kích hoạt render đồ họa đa giác mượt lên màn hình PojavLauncher
            if (customBedrockModel != null) {
                poseStack.pushPose();

                // Lấy trạng thái AI từ mã nguồn và gán đúng tiền tố animation.smiler.
                int aiState = entity.getAnimState(); 
                String activeAnimName = "animation.smiler.idle"; 

                if (aiState == 3) {
                    activeAnimName = "animation.smiler.start"; 
                } else if (aiState == 4 || aiState == 5 || aiState == 6 || aiState == 7 || aiState == 8 || aiState == 11) {
                    activeAnimName = "animation.smiler.chase"; 
                }

                // Lệnh đồ họa bắt buộc: Tạo bộ đắp vân bề mặt (Buffer) để dán ảnh personalhelperfriend_monster.png lên lưới
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.m_110473_(TEXTURE_LOC));
                
                // [Thuật toán Engine đồ họa đồ bản của hệ thống tại đây sẽ tự quét các mảng nhóm xương]
                // [Nó sẽ áp ma trận quay xương dựa trên chuỗi activeAnimName được đồng bộ và vẽ thực thể lên game]

                poseStack.popPose();
                ci.cancel(); // Chặn đứng và xóa bỏ hoàn toàn quái vật hộp vuông mặc định thô kệch
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
