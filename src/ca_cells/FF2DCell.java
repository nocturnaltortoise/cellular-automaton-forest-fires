//*******************************************
//* 
//*    Forest Fire CA - a Cell object 
//*    Dr Martin Bayley 18/02/11
//*    Module COM2005
//*
//*******************************************

package ca_cells;                 // assign class to ca_cells package (implications for class dir. structure)

import java.util.Random;

public class FF2DCell{           // declare class 

  //******************************************************************************  
  //  Constructor - initialise all private state varibles  to false (zero) state 
  //******************************************************************************

  public FF2DCell(){       // At construction initialise a 3x3 private state array to all false
    
    int i; int j;           

    for(i=0;i<3;i++){
      for(j=0;j<3;j++){
        neighbourStates[i][j]=false;
      }
    }
  }
  
  //******************************************************************************************
  //  Member functions to allow the Grid class (next level up-defining a group of cells)
  //  to set up the private cell-state variables for the neighborhood and report its own state 
  //******************************************************************************************

  public void setState(int xPos, int yPos, boolean setStateIn){    // set a state in the 3x3 state array
    neighbourStates[xPos][yPos]=setStateIn;
  }

  public boolean getState(){                              // return the cell state (pos 1,1 of private state array)
    return(neighbourStates[1][1]);
  }

  public void setFuelLevel(int newFuelLevel){
    initFuelLevel = newFuelLevel;
  }

  public void startRefractory() {
    if (mode != MODES.SIMPLE) {
      cellState.setState(FF2DCellState.CELL_STATE.REFRACTORY);
      refractoryIterations = refractoryPeriod;
    }
  }

  // Carry out next step in refractory process
  public void refractoryStep() {
    if (cellState.getState() == FF2DCellState.CELL_STATE.REFRACTORY) {
      if (refractoryIterations > 1) {
        refractoryIterations--;
        cellState.setRefractory(refractoryIterations, refractoryPeriod);
      } else {
        // Refractory period has finished so replenish fuel
        fuelLevel = initFuelLevel;
//        inRefractory = false;
        cellState.setState(FF2DCellState.CELL_STATE.EXCITABLE);
      }
    }
  }

  public int getFuelLevel(){
    return fuelLevel;
  }

  public void decrementFuelLevel(){
      fuelLevel--;
  }

  //****************************************************************************
  //  Create a next-state method to encapsulate FF rules and return the 
  //  next generational state for that cell. 
  //****************************************************************************

  public boolean nextState(){

    onFireNeighbours = 0;
    boolean nextState = neighbourStates[1][1];   // set the default return state to be the unchanged current state

    // count the number of on fire neighbours
    
    for(int i=0;i<3;i++){
      for(int j=0;j<3;j++){
        if((i!=1)||(j!=1)){              // don't include the current cell state
          if (neighbourStates[i][j]) {
            onFireNeighbours++;
          }
        }
      }
    }

    switch (mode) {
      case SIMPLE:
        if ( cellState.getState() != FF2DCellState.CELL_STATE.ALIVE) {
          nextState = shouldCatchFire();
        }
        else {
          nextState = fuelLevel > 0;
        }

        if (!nextState) {
          cellState.setState(FF2DCellState.CELL_STATE.DEAD);
        }

        break;
      case REFRACTORY:
        refractoryStep();
        if (cellState.getState() == FF2DCellState.CELL_STATE.ALIVE) {
          nextState = fuelLevel > 0;
        }
        else {
          nextState = shouldCatchFire() && fuelLevel > 0;
        }
        break;
      case PROBABILISTIC:
        refractoryStep();
        if (cellState.getState() == FF2DCellState.CELL_STATE.ALIVE) {
          nextState = fuelLevel > 0;
        }
        else {
          nextState = shouldCatchFireFromNeighbours() && fuelLevel > 0;
        }
        break;
    }

    if (nextState) {
      cellState.setState(FF2DCellState.CELL_STATE.ALIVE);
    }


    if ( cellState.getState() == FF2DCellState.CELL_STATE.ALIVE ) System.out.println(cellState.getState());
    return nextState;
  }


  public boolean inRefractoryCycle() {
    return cellState.getState() == FF2DCellState.CELL_STATE.REFRACTORY;
  }

  public FF2DCellState getCellState() {
    return cellState;
  }

  private boolean shouldCatchFire() {
    return onFireNeighbours >= 1 && fuelLevel > 0;
  }

  private boolean shouldCatchFireFromNeighbours() {
    double neighbourCatchingFireProbability = ((double)onFireNeighbours / (double)totalNeighbours) * 1.5;
//    double neighbourCatchingFireProbability = 1 / (((double)totalNeighbours+1) - (double)onFireNeighbours);
    double randomNumber = randomGenerator.nextDouble();
    return randomNumber <= neighbourCatchingFireProbability;
  }

  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean[][] neighbourStates = new boolean[3][3];  // the private 3x3 array of cell and neighbour states
  private int refractoryPeriod = 20;
  private int refractoryIterations = refractoryPeriod;
  private Random randomGenerator = new Random(System.currentTimeMillis());
  private int maxFuelLevel = 10;
  private int initFuelLevel = randomGenerator.nextInt(maxFuelLevel);
//  to disable random generation, change initFuelLevel to a value like 30 or 50
  private int fuelLevel = initFuelLevel;
  private int totalNeighbours = 8;
  private int onFireNeighbours = 0;

  private FF2DCellState cellState = new FF2DCellState();

  private enum MODES {
    SIMPLE, REFRACTORY, PROBABILISTIC
  }

  // Change the operation mode here:
  private MODES mode = MODES.REFRACTORY;
}