package io.github.kidofcubes.irisshaderdof.client;

import io.github.kidofcubes.irisshaderdof.DepthHolderThing;
import io.github.kidofcubes.irisshaderdof.TimeHolderThing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class IrisShaderDOFClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */

    private static KeyBinding plusBinding;
    private static KeyBinding minusBinding;
    private static KeyBinding bigPlusBinding;
    private static KeyBinding bigMinusBinding;
    private static KeyBinding smallPlusBinding;
    private static KeyBinding smallMinusBinding;
    private static KeyBinding toggleBinding;
    private static KeyBinding toggleLock;

    private static KeyBinding dofToggleLock;
    private static KeyBinding dofToggleOverride;
    private static KeyBinding timeToggle;

    private static KeyBinding smallModifier;
    private static KeyBinding bigModifier;

    private static KeyBinding add;
    private static KeyBinding minus;

    private static final float bigMult = 10.0f;
    private static final float smallMult = 0.1f;

    private static float getMult(){
        if(smallModifier.isPressed()){
            return smallMult;
        }
        if(bigModifier.isPressed()){
            return bigMult;
        }
        return 1.0f;
    }
    @Override
    public void onInitializeClient() {


        dofToggleLock = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "dofToggleLock", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_0, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));
        dofToggleOverride = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "dofToggleOverride", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_PERIOD, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));

        timeToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "timeToggle", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_NO_API, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));

        smallModifier = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "smallModifier", // The translation key of the keybinding's name
                InputUtil.Type.MOUSE, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_NO_API, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));
        bigModifier = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "bigModifier", // The translation key of the keybinding's name
                InputUtil.Type.MOUSE, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_NO_API, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));

        add = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "add", // The translation key of the keybinding's name
                InputUtil.Type.MOUSE, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_UP, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));
        minus = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "minus", // The translation key of the keybinding's name
                InputUtil.Type.MOUSE, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_DOWN, // The keycode of the key
                "IrisDOF" // The translation key of the keybinding's category.
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (dofToggleLock.wasPressed()&&client.player!=null) {
                DepthHolderThing.locked=!DepthHolderThing.locked;
                client.player.sendMessage(Text.literal("Toggled DOF lock to "+DepthHolderThing.locked), true);
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (dofToggleOverride.wasPressed()&&client.player!=null) {
                DepthHolderThing.manualDOF=!DepthHolderThing.manualDOF;
                client.player.sendMessage(Text.literal("Toggled DOF override to "+DepthHolderThing.manualDOF), true);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (timeToggle.wasPressed()&&client.player!=null) {
                TimeHolderThing.forceTime = !TimeHolderThing.forceTime;
                client.player.sendMessage(Text.literal("Toggled TIME override to "+TimeHolderThing.forceTime), true);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (add.wasPressed()&&client.player!=null) {
                if(DepthHolderThing.manualDOF) {
                    DepthHolderThing.setDepthValue(DepthHolderThing.getDepthValue() + (getMult()*1));
                    client.player.sendMessage(Text.literal("Changed depth value to " + DepthHolderThing.getDepthValue()), true);
                }
                if(TimeHolderThing.forceTime) {
                    TimeHolderThing.setTime((int) (TimeHolderThing.getTime() + (getMult()*10)));
                    client.player.sendMessage(Text.literal("Changed shader time to " + TimeHolderThing.getTime()), true);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (minus.wasPressed()&&client.player!=null) {
                if(DepthHolderThing.manualDOF) {
                    DepthHolderThing.setDepthValue(DepthHolderThing.getDepthValue() + (getMult()*-1));
                    client.player.sendMessage(Text.literal("Changed depth value to " + DepthHolderThing.getDepthValue()), true);
                }
                if(TimeHolderThing.forceTime) {
                    TimeHolderThing.setTime((int) (TimeHolderThing.getTime() + (getMult()*-10)));
                    client.player.sendMessage(Text.literal("Changed shader time to " + TimeHolderThing.getTime()), true);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(TimeHolderThing.forceTime&&MinecraftClient.getInstance().world!=null) {
                MinecraftClient.getInstance().world.setTimeOfDay(TimeHolderThing.getTime());
            }
        });
    }
}
