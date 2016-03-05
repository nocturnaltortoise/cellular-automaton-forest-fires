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
        cellState[i][j]=false;
      }
    }
  }
  
  //******************************************************************************************
  //  Member functions to allow the Grid class (next level up-defining a group of cells)
  //  to set up the private cell-state variables for the neighborhood and report its own state 
  //******************************************************************************************

  public void setState(int xPos, int yPos, boolean setStateIn){    // set a state in the 3x3 state array
    cellState[xPos][yPos]=setStateIn;
  }

  public boolean getState(){                              // return the cell state (pos 1,1 of private state array)
    return(cellState[1][1]);
  }


  public void startRefractory() {
    if (mode != MODES.SIMPLE) {
      inRefractory = true;
      refractoryIterations = refractoryPeriod;
    }
  }

  // Carry out next step in refractory process
  public void refractoryStep() {
    if (inRefractory) {
      if (refractoryIterations > 1) {
        refractoryIterations--;
      } else {
        // Refractory period has finished so replenish fuel
        fuelLevel = initFuelLevel;
        inRefractory = false;
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
    boolean nextState = cellState[1][1];   // set the default return state to be the unchanged current state

    // count the number of on fire neighbours
    
    for(int i=0;i<3;i++){
      for(int j=0;j<3;j++){
        if((i!=1)||(j!=1)){              // don't include the current cell state
          if (cellState[i][j]) {
            onFireNeighbours++;
          }
        }
      }
    }

    switch (mode) {
      case SIMPLE:
        nextState = shouldCatchFire();
        break;
      case REFRACTORY:
        refractoryStep();
        nextState = shouldCatchFire();
        break;
      case PROBABILISTIC:
        refractoryStep();
        nextState = shouldCatchFireFromNeighbours() && fuelLevel > 0;
        break;
    }

    return nextState;
  }


  public boolean inRefractoryCycle() {
    return inRefractory;
  }

  private boolean shouldCatchFire() {
    return onFireNeighbours >= 1 && fuelLevel > 0;
  }

  private boolean shouldCatchFireFromNeighbours() {
    double neighbourCatchingFireProbability = 1 / (((double)totalNeighbours+1) - (double)onFireNeighbours);
    double randomNumber = randomGenerator.nextDouble();
    return randomNumber <= neighbourCatchingFireProbability;
  }

  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean[][] cellState = new boolean[3][3];  // the private 3x3 array of cell and neighbour states
  private int refractoryPeriod = 4;
  private int refractoryIterations = refractoryPeriod;
  private int initFuelLevel = 10;
  private int fuelLevel = initFuelLevel;
  private int totalNeighbours = 8;
  private int onFireNeighbours = 0;
  private Random randomGenerator = new Random(System.currentTimeMillis());
  private boolean inRefractory = false;

  private enum MODES {
    SIMPLE, REFRACTORY, PROBABILISTIC;
  }

  // Change the operation mode here:
  private MODES mode = MODES.SIMPLE;
}