package ca_cells;

import java.awt.*;

/**
 * Created by joshua on 07/03/16.
 */
public class FF2DCellState {
    public enum CELL_STATE {
        ALIVE, DEAD, REFRACTORY;
    }

    private CELL_STATE cellState;

    public Color getColour() {
        switch (cellState) {
            case ALIVE:
                return Color.WHITE;
            case DEAD:
                return Color.BLACK;
            case REFRACTORY:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }
}
