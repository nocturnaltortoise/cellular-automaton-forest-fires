package ca_cells;

/**
 * Created by joshua on 10/03/16.
 */
public class FF2DConstants {
    public static final boolean USE_TERRAIN_FUEL_DISTRIBUTION = true;

    public enum MODE {
        SIMPLE, REFRACTORY, PROBABILISTIC
    }

    // Change the operation mode here:
    public static MODE mode = MODE.PROBABILISTIC;
}
