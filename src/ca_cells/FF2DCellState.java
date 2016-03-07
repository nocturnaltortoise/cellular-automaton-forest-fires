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

    public CELL_STATE getState() {
        return cellState;
    }

    public void setRefractory(int refractoryIterations, int refractoryTotal) {
        this.refractoryIterations = refractoryIterations;
        this.refractoryTotal = refractoryTotal;
    }

    public enum CELL_STATE {
        EXCITABLE, ALIVE, REFRACTORY, DEAD;
    }

    private CELL_STATE cellState;

    private int refractoryIterations;
    private int refractoryTotal;

    public Color getColour() {
        switch (cellState) {
            case ALIVE:
                return Color.RED;
            case EXCITABLE:
                return Color.GREEN;
            case REFRACTORY:
                double rValue = ((double)(refractoryTotal - refractoryIterations) / (double)refractoryTotal) * 255;
//                Color c = new Color((int)rValue, 0, 0);
                Color c = new Color(0, (int)rValue, 0);
                return c;
            case DEAD:
                return Color.BLACK;
            default:
                return Color.BLACK;
        }
    }
}
