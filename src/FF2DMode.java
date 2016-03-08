/**
 * Created by joshua on 08/03/16.
 */
public class FF2DMode {
    public enum MODE {SIMPLE, REFRACTORY, PROBABILISTIC}

    private static MODE mode = MODE.SIMPLE;

    public static void setMode(MODE mode) {
        FF2DMode.mode = mode;
    }

    public static MODE getMode() {
        return mode;
    }
}


