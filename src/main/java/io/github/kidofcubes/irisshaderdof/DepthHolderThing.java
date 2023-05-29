package io.github.kidofcubes.irisshaderdof;

import static java.lang.Math.max;

public class DepthHolderThing {

    public static boolean manualDOF=false;
    public static boolean locked = false;
    public static boolean hasBeenUpdated = false;
    public static int sinceLast = 0;

    private static float depthValue = 10.0f;
    public static float getDepthValue(){
        return depthValue;
    }

    public static void setDepthValue(float depthValueNew){
        depthValue=max(depthValueNew,0.1f);
        hasBeenUpdated=false;
    }
}
