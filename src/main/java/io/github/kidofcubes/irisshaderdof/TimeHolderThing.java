package io.github.kidofcubes.irisshaderdof;

public class TimeHolderThing {
    public static boolean forceTime=false;
    private static int time = 6000;
    public static int getTime(){
        return time;
    }

    public static void setTime(int newTime){
        time=newTime%24000;
    }
}
