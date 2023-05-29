package io.github.kidofcubes.irisshaderdof.mixin;


import net.coderbot.iris.gl.IrisRenderSystem;
import net.coderbot.iris.gl.texture.InternalTextureFormat;
import net.coderbot.iris.gl.texture.PixelType;
import net.coderbot.iris.postprocess.CenterDepthSampler;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL21C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.function.IntSupplier;

import static io.github.kidofcubes.irisshaderdof.DepthHolderThing.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

@Mixin(CenterDepthSampler.class)
public abstract class IrisDepthMixin {


    private static final float near = 0.05F; //i think it is anyway
    @Accessor(remap = false)
    abstract int getAltTexture();
    @Accessor(remap = false)
    abstract void setHasFirstSample(boolean value);

    @Inject(at = @At("HEAD"), method = "sampleCenterDepth", cancellable = true, remap = false)
    private void onSample(CallbackInfo ci){
        if(manualDOF&&!hasBeenUpdated&&!locked){
            ByteBuffer buf = BufferUtils.createByteBuffer(4);
            float far = MinecraftClient.getInstance().options.getViewDistance().getValue() * 16.0f;

            buf.putFloat(linearToDepth(far,getDepthValue()));
            buf.flip();
            InternalTextureFormat format = InternalTextureFormat.R32F;



            IrisRenderSystem.texImage2D(getAltTexture(), GL_TEXTURE_2D, 0, format.getGlFormat(), 1, 1, 0, format.getPixelFormat().getGlFormat(), PixelType.FLOAT.getGlFormat(), buf);
            IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_MIN_FILTER, GL21C.GL_LINEAR); //i think i need to do these? not sure tbh
            IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_MAG_FILTER, GL21C.GL_LINEAR);
            IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_WRAP_S, GL21C.GL_CLAMP_TO_EDGE);
            IrisRenderSystem.texParameteri(getAltTexture(), GL_TEXTURE_2D, GL21C.GL_TEXTURE_WRAP_T, GL21C.GL_CLAMP_TO_EDGE);
//            setHasFirstSample(false); //??
//            System.out.println("updated dof");
            hasBeenUpdated=true;
        }
        if(manualDOF||locked){
            ci.cancel();
        }
    }

    float linearToDepth(float far, float linear){
        return ((IrisDepthMixin.near *far)-(linear*far))/(linear*(IrisDepthMixin.near -far));
    }
}
