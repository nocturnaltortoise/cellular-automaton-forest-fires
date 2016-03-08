//*******************************************
//* 
//*    Forest Fire CA - a Cell object 
//*    Dr Martin Bayley 18/02/11
//*    Module COM2005
//*
//*******************************************

package ca_cells;                 // assign class to ca_cells package (implications for class dir. structure)

import ca_grids.FF2DGrid;

import java.util.Random;

public class FF2DCell{           // declare class 

  //******************************************************************************  
  //  Constructor - initialise all private state variables  to false (zero) state
  //******************************************************************************

  public FF2DCell(int x, int y){       // At construction initialise a 3x3 private state array to all false

    this.xPos = x;
    this.yPos = y;
    //uncomment this to turn on random terrain generation
//    initFuelLevel = FF2DGrid.findNearestSeedPoint(this.xPos, this.yPos, FF2DGrid.seedPoints)[2];
    initFuelLevel = 30;
    fuelLevel = initFuelLevel;

    for(int i=0;i<3;i++){
      for(int j=0;j<3;j++){
        neighbourStates[i][j]=false;
      }
    }

    // If initial fuel level is 0, set cell state to dead so that it can never start again (prevent alternating)
    if (initFuelLevel == 0) {
      cellState.setState(FF2DCellState.CELL_STATE.DEAD);
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
    if ( cellState.getState() != FF2DCellState.CELL_STATE.DEAD) {
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

    if ( mode != MODES.SIMPLE ) {
      refractoryStep();
    }

    if ( cellState.getState() == FF2DCellState.CELL_STATE.ALIVE) {
      nextState = fuelLevel > 0;
    }
    else {
      switch (mode) {
        case SIMPLE:
          nextState = shouldCatchFire();
          break;
        case REFRACTORY:
          nextState = shouldCatchFire() && fuelLevel > 0;
          break;
        case PROBABILISTIC:
          nextState = shouldCatchFireFromNeighbours() && fuelLevel > 0;
          break;
      }
    }

    if (nextState) {
      cellState.setState(FF2DCellState.CELL_STATE.ALIVE);
    }

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
//    double neighbourCatchingFireProbability = 1 / ((double)totalNeighbours + 1) - (double)onFireNeighbours;
    double randomNumber = randomGenerator.nextDouble();
//    double threshold = 0.5;
    return randomNumber <= neighbourCatchingFireProbability;
  }

  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean[][] neighbourStates = new boolean[3][3];  // the private 3x3 array of cell and neighbour states
  private int refractoryPeriod = 30;
  private int refractoryIterations = refractoryPeriod;
  private Random randomGenerator = new Random(System.currentTimeMillis());
  private int maxFuelLevel = 30;
  private int initFuelLevel;
//  private int initFuelLevel = randomGenerator.nextInt(maxFuelLevel);

//  to disable random generation, change initFuelLevel to a value like 30 or 50
  private int fuelLevel;
  private int totalNeighbours = 8;
  private int onFireNeighbours = 0;
  private int xPos;
  private int yPos;

  private FF2DCellState cellState = new FF2DCellState();

  private enum MODES {
    SIMPLE, REFRACTORY, PROBABILISTIC
  }

  // Change the operation mode here:
  private MODES mode = MODES.PROBABILISTIC;
}