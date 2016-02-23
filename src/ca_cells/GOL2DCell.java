//*******************************************
//* 
//* GOL CA - a Cell object 
//*    Dr Martin Bayley 18/02/11
//*    Module COM2005
//*
//*******************************************

package ca_cells;                 // assign class to ca_cells package (implications for class dir. structure)

public class GOL2DCell{           // declare class 

  //******************************************************************************  
  //  Constructor - initialise all private state varibles  to false (zero) state 
  //******************************************************************************

  public GOL2DCell(){       // At construction initialise a 3x3 private state array to all false
    
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

  //****************************************************************************
  //  Create a next-state method to encapsulate GOL rules and return the 
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

    int nLiveN=0;                        // local to store no. of live neighbour states
    int i; int j;                        // itterators
    boolean nextState=cellState[1][1];   // set the default return state to be the unchanged current state 

    // count the number of live neighbours
    // ***NOTE - To change to a von Neumann neighbourhood alter the following set of nested loops.....
    
    for(i=0;i<3;i++){
      for(j=0;j<3;j++){
        if((i!=1)||(j!=1)){              // don't include the current cell state
          if (cellState[i][j]==true) {
            nLiveN++ ;
          }
        }
      }
    }


   // Nested Logic to recognise any new state change based on the rules.
   // ***NOTE - To change life/death rules change the values in the following conditions.....

    if (cellState[1][1]==true){             // if the current cell state is true ....
      if((nLiveN<2)||(nLiveN>3)) {          // kill the state to false if nLiveN is not 2 or 3 
        nextState=false; 
      }
    } 
    else {                                  // else if current cell state is false .....
      if (nLiveN==3) { nextState=true; }    // check if nLiveN =3 to make the cell come alive (true)
    }
    return(nextState);	                    // pass out the new nextState value 
  }

  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean[][] cellState= new boolean[3][3];  // the private 3x3 array of cell and neighbour states
}