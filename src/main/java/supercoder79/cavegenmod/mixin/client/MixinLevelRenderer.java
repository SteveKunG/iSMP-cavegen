package supercoder79.cavegenmod.mixin.client;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int ticks;

    @Shadow
    @Final
    private float[] rainSizeX;

    @Shadow
    @Final
    private float[] rainSizeZ;

    @Shadow
    @Final
    private static ResourceLocation RAIN_LOCATION;

    @Shadow
    @Final
    private static ResourceLocation SNOW_LOCATION;

    @Overwrite
    private void renderSnowAndRain(LightTexture lightTexture, float partialTicks, double xPos, double yPos, double zPos)
    {
        var h = this.minecraft.level.getRainLevel(partialTicks);

        if (!(h <= 0.0F))
        {
            lightTexture.turnOnLightLayer();
            Level level = this.minecraft.level;
            var xInt = Mth.floor(xPos);
            var yInt = Mth.floor(yPos);
            var zInt = Mth.floor(zPos);
            var tesselator = Tesselator.getInstance();
            var bufferBuilder = tesselator.getBuilder();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            var l = 5;

            if (Minecraft.useFancyGraphics())
            {
                l = 10;
            }

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            var m = -1;
            var n = this.ticks + partialTicks;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            var mutableBlockPos = new BlockPos.MutableBlockPos();

            for (var o = zInt - l; o <= zInt + l; ++o)
            {
                for (var p = xInt - l; p <= xInt + l; ++p)
                {
                    for (var yyy = yInt; yyy <= yInt; ++yyy)
                    {
                        var q = (o - zInt + 16) * 32 + p - xInt + 16;
                        var r = this.rainSizeX[q] * 0.5D;
                        var s = this.rainSizeZ[q] * 0.5D;
                        mutableBlockPos.set(p, yyy, o);
                        var biome = level.getBiome(mutableBlockPos);

                        if (biome.getPrecipitation() != Biome.Precipitation.NONE)
                        {
                            var t = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, mutableBlockPos).getY();
                            var u = yyy - l;
                            var v = yyy + l;

                            if (u < t)
                            {
                                u = t;
                            }

                            if (v < t)
                            {
                                v = t;
                            }

                            var w = t;

                            if (t < yyy)
                            {
                                w = yyy;
                            }

                            if (u != v)
                            {
                                var random = new Random(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761);
                                mutableBlockPos.set(p, u, o);
                                var x = biome.getTemperature(mutableBlockPos);
                                float z;
                                float ad;

                                if (x >= 0.15F)
                                {
                                    if (m != 0)
                                    {
                                        if (m >= 0)
                                        {
                                            tesselator.end();
                                        }
                                        m = 0;
                                        RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                                        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                    }

                                    var y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 31;
                                    z = -(y + partialTicks) / 32.0F * (3.0F + random.nextFloat());
                                    var aa = p + 0.5D - xPos;
                                    var ab = o + 0.5D - zPos;
                                    var ac = (float)Math.sqrt(aa * aa + ab * ab) / l;
                                    ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * h;
                                    mutableBlockPos.set(p, w, o);
                                    var ae = LevelRenderer.getLightColor(level, mutableBlockPos);
                                    bufferBuilder.vertex(p - xPos - r + 0.5D, v - yPos, o - zPos - s + 0.5D).uv(0.0F, u * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).uv2(ae).endVertex();
                                    bufferBuilder.vertex(p - xPos + r + 0.5D, v - yPos, o - zPos + s + 0.5D).uv(1.0F, u * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).uv2(ae).endVertex();
                                    bufferBuilder.vertex(p - xPos + r + 0.5D, u - yPos, o - zPos + s + 0.5D).uv(1.0F, v * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).uv2(ae).endVertex();
                                    bufferBuilder.vertex(p - xPos - r + 0.5D, u - yPos, o - zPos - s + 0.5D).uv(0.0F, v * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).uv2(ae).endVertex();
                                }
                                else
                                {
                                    if (m != 1)
                                    {
                                        if (m >= 0)
                                        {
                                            tesselator.end();
                                        }
                                        m = 1;
                                        RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                                        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                    }

                                    var af = -((this.ticks & 511) + partialTicks) / 512.0F;
                                    z = (float)(random.nextDouble() + n * 0.01D * (float)random.nextGaussian());
                                    var ah = (float)(random.nextDouble() + n * (float)random.nextGaussian() * 0.001D);
                                    var ai = p + 0.5D - xPos;
                                    var aj = o + 0.5D - zPos;
                                    ad = (float)Math.sqrt(ai * ai + aj * aj) / l;
                                    var al = ((1.0F - ad * ad) * 0.3F + 0.5F) * h;
                                    mutableBlockPos.set(p, w, o);
                                    var am = LevelRenderer.getLightColor(level, mutableBlockPos);
                                    var an = am >> 16 & '\uffff';
                    var ao = am & '\uffff';
                    var ap = (an * 3 + 240) / 4;
                    var aq = (ao * 3 + 240) / 4;
                    bufferBuilder.vertex(p - xPos - r + 0.5D, v - yPos, o - zPos - s + 0.5D).uv(0.0F + z, u * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).uv2(aq, ap).endVertex();
                    bufferBuilder.vertex(p - xPos + r + 0.5D, v - yPos, o - zPos + s + 0.5D).uv(1.0F + z, u * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).uv2(aq, ap).endVertex();
                    bufferBuilder.vertex(p - xPos + r + 0.5D, u - yPos, o - zPos + s + 0.5D).uv(1.0F + z, v * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).uv2(aq, ap).endVertex();
                    bufferBuilder.vertex(p - xPos - r + 0.5D, u - yPos, o - zPos - s + 0.5D).uv(0.0F + z, v * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).uv2(aq, ap).endVertex();
                                }
                            }
                        }
                    }
                }
            }

            if (m >= 0)
            {
                tesselator.end();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            lightTexture.turnOffLightLayer();
        }
    }
}