//*******************************************
//* 
//*    Forest Fire CA - a Cell object 
//*    Dr Martin Bayley 18/02/11
//*    Module COM2005
//*
//*******************************************

package ca_cells;                 // assign class to ca_cells package (implications for class dir. structure)

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

  public void setBurntOut(boolean isBurntOut){
      burntOut = isBurntOut;
      if (burntOut) {
        // If burnt out, start refactory cycle
        refactoryIterations = refactoryPeriod;
      }
  }

  public boolean getBurntOut(){
      return burntOut;
  }

//  public void incrementOnFireForCount(){
//      onFireForCount++;
//  }
//
//  public void setOnFireForCount(int updatedCount){
//      onFireForCount = updatedCount;
//  }
//
//  public int getOnFireForCount(){
//      return onFireForCount;
//  }

  public int getFuelLevel(){
    return fuelLevel;
  }

  public void setFuelLevel(int updatedLevel){
    fuelLevel = updatedLevel;
  }

  public void decrementFuelLevel(){
      fuelLevel--;
  }

  //****************************************************************************
  //  Create a next-state method to encapsulate FF rules and return the 
  //  next generational state for that cell. 
  //****************************************************************************

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Conways Game of Life Rules:
//
// 1. DECISION METRIC -Sum all live neighbours.
// 2. KILLING RULE    -If live, and nLiveNeighbors is not 2 or 3 then next state changes to dead.
// 3. LIVING RULE     -If dead, and nLiveNeighbors=3 then state changes to live next step. 
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////

  public boolean nextState(){

    int onFireNeighbours = 0;
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

    // if there are any neighbours on fire, catch fire
    nextState = !burntOut && onFireNeighbours >= 1;

    if (refactoryIterations > 0) {
      refactoryIterations--;
    }
    else {
      // Refactory period has finished so replenish fuel
      fuelLevel = initFuelLevel;
    }

    return nextState;
  }

  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean[][] cellState = new boolean[3][3];  // the private 3x3 array of cell and neighbour states
  private int refactoryPeriod = 20;
  private int refactoryIterations = 0;
  private boolean burntOut;
  private int initFuelLevel = 20;
  private int fuelLevel = initFuelLevel;
}