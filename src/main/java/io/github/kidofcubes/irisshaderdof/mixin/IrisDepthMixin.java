package io.github.kidofcubes.irisshaderdof.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import net.coderbot.iris.gl.IrisRenderSystem;
import net.coderbot.iris.gl.framebuffer.GlFramebuffer;
import net.coderbot.iris.gl.program.Program;
import net.coderbot.iris.gl.program.ProgramSamplers;
import net.coderbot.iris.gl.program.ProgramUniforms;
import net.coderbot.iris.gl.texture.DepthCopyStrategy;
import net.coderbot.iris.gl.texture.InternalTextureFormat;
import net.coderbot.iris.gl.texture.PixelType;
import net.coderbot.iris.postprocess.CenterDepthSampler;
import net.coderbot.iris.postprocess.FullScreenQuadRenderer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

import static io.github.kidofcubes.irisshaderdof.DepthHolderThing.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

@Mixin(CenterDepthSampler.class)
public abstract class IrisDepthMixin {


    private static final float near = 0.05F; //i think it is anyway
    @Accessor(remap = false)
    abstract int getAltTexture();
    @Accessor(remap = false)
    abstract int getTexture();
    @Accessor(remap = false)
    abstract void setHasFirstSample(boolean value);
    @Accessor(remap = false)
    abstract GlFramebuffer getFramebuffer();
    @Accessor(remap = false)
    abstract Program getProgram();

    public void testRead(){
    }

    @Inject(at = @At("HEAD"), method = "sampleCenterDepth", cancellable = true, remap = false)
    private void onSample(CallbackInfo ci){
        if(!locked&&!hasBeenUpdated) lockedDepthValue=-1;
        if(locked&&!hasBeenUpdated&&!manualDOF){
            if(lockedDepthValue==-1) {
                getFramebuffer().bind();
                getProgram().use();
                RenderSystem.viewport(0, 0, 1, 1);
                FullScreenQuadRenderer.INSTANCE.render();


                float[] floatz = new float[1];

                IrisRenderSystem.readPixels(0,0,1,1,GL11C.GL_RED, GL11C.GL_FLOAT,floatz);
                lockedDepthValue=floatz[0];

                DepthCopyStrategy.fastest(false).copy(getFramebuffer(), getTexture(), null, getAltTexture(), 1, 1);
                ProgramUniforms.clearActiveUniforms();
                ProgramSamplers.clearActiveSamplers();
                MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            }
            setDepth(lockedDepthValue,false);
            ci.cancel();
            hasBeenUpdated=true;
            return;
        }


        if(manualDOF&&!hasBeenUpdated&&!locked){
            setDepth(getDepthValue(), true);
//            setHasFirstSample(false); //??
            hasBeenUpdated=true;
        }
        if(manualDOF||locked){
            ci.cancel();
        }
    }
    void setDepth(float num, boolean unlinear){
        ByteBuffer buf = BufferUtils.createByteBuffer(4);

        if(unlinear) buf.putFloat(linearToDepth((MinecraftClient.getInstance().options.getViewDistance().getValue() * 16.0f), num));
        else buf.putFloat(num);

        buf.flip();
        InternalTextureFormat format = InternalTextureFormat.R32F;
        IrisRenderSystem.texImage2D(getAltTexture(), GL_TEXTURE_2D, 0, format.getGlFormat(), 1, 1, 0, format.getPixelFormat().getGlFormat(), PixelType.FLOAT.getGlFormat(), buf);
//        IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_MIN_FILTER, GL21C.GL_LINEAR); //i think i need to do these? not sure tbh
//        IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_MAG_FILTER, GL21C.GL_LINEAR);
//        IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_WRAP_S, GL21C.GL_CLAMP_TO_EDGE);
//        IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_WRAP_T, GL21C.GL_CLAMP_TO_EDGE);
    }

    float linearToDepth(float far, float linear){
        return ((IrisDepthMixin.near *far)-(linear*far))/(linear*(IrisDepthMixin.near -far));
    }
}
