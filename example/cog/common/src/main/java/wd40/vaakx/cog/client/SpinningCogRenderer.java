package wd40.vaakx.cog.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import wd40.vaakx.cog.common.entities.SpinningCog;
import wd40.vaakx.cog.common.items.Items;

/**
 * Draws {@link SpinningCog} as the cog item, floating + spinning around the
 * Y axis. Pure cog code - lubricant doesn't ship this; cog registers it via
 * {@link wd40.lubricant.api.client.EntityRenderers#entity}.
 */
public final class SpinningCogRenderer extends EntityRenderer<SpinningCog> {

    private static final ResourceLocation NO_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    private final ItemRenderer itemRenderer;

    public SpinningCogRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SpinningCog entity, float entityYaw, float partialTicks, PoseStack pose,
                       MultiBufferSource buffer, int packedLight) {
        float age = entity.tickCount + partialTicks;

        pose.pushPose();
        // Slight bob.
        pose.translate(0.0, 0.1 + Math.sin(age * 0.1f) * 0.05f, 0.0);
        // Spin on Y axis - 4 degrees per tick = ~80 degrees/sec.
        pose.mulPose(Axis.YP.rotationDegrees(age * 4.0f));

        ItemStack stack = new ItemStack(Items.COG.get());
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, pose, buffer, entity.level(), entity.getId());

        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpinningCog entity) {
        return NO_TEXTURE;
    }
}
