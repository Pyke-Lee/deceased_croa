package kr.pyke.deceased_croa.data;

public class GameTimeController {
    private static float scale = 1.f;
    private static double accumulator = 0.d;
    private static boolean frozen = false;

    public static float getScale() { return GameTimeController.scale; }
    public static void setScale(float scale) { GameTimeController.scale = Math.max(0.f, scale); }

    public static boolean isFrozen() { return frozen; }
    public static void setFrozen(boolean frozen) { GameTimeController.frozen = frozen; }

    public static long consumeDelta() {
        if (GameTimeController.frozen) { return 0L; }

        GameTimeController.accumulator += GameTimeController.scale;
        long whole = (long) GameTimeController.accumulator;
        GameTimeController.accumulator -= whole;
        return whole;
    }

    public static void reset() {
        GameTimeController.accumulator = 0.d;
    }
}
