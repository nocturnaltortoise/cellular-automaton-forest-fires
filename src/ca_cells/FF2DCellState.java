package ca_cells;

import java.awt.*;

/**
 * Created by joshua on 07/03/16.
 */
public class FF2DCellState {

    public FF2DCellState() {
        cellState = CELL_STATE.EXCITABLE;
    }

    public FF2DCellState(CELL_STATE state) {
        cellState = state;
    }

    public void setState(CELL_STATE state) {
        cellState = state;
    }

    public enum CELL_STATE {
        EXCITABLE, ALIVE, REFRACTORY;
    }

    private CELL_STATE cellState;

    public Color getColour() {
        switch (cellState) {
            case ALIVE:
                return Color.WHITE;
            case EXCITABLE:
                return Color.GREEN;
            case REFRACTORY:
                return Color.PINK;
            default:
                return Color.BLACK;
        }
    }
}
