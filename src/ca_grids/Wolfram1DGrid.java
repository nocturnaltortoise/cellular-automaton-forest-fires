/*********************************************************************************************** 
 * 
 * Generation Class - this is the second hierachy and defines the basic orgaisation of cells
 * initialises them (both in the constructor)and then controls the generational progession. 
 * It also implements a boundary condition (in this case we assume periodic (wrapping) BCs)
 * A method to pass an array of current states allows the Main program to manage output...    
 *
 ************************************************************************************************/

package ca_grids;

import ca_cells.Wolfram1DCell;

public class Wolfram1DGrid{
 
  //***************************************************************************************
  // Constructor - this ininitialises (to false) an array of cells and seeds (to true) 
  // the most central cell (and associated neighbours neighborhood states). 
  //***************************************************************************************

  public Wolfram1DGrid(int ruleNumberIn, int sizeIn) {
    
    //initialisations....
    size1D=sizeIn;
    genNumber =0;
    ruleNumber=ruleNumberIn;

    //locals.....
    int seedPoint;
    seedPoint =1+(int)((size1D-1)/2);
    int i;
   
    cells= new Wolfram1DCell[size1D];
    for(i=0;i<size1D;i++)  { cells[i]=new Wolfram1DCell();}

    // single, central seed point setup - also set neighbor states..... 
    cells[seedPoint].setState(true); 
    cells[(seedPoint+1)].setLeftState(true);
    cells[(seedPoint-1)].setRightState(true);

    for(int n=0; n<sizeIn/2; n+=2) {
        cells[seedPoint + n].setState(true);
        cells[(seedPoint + 1) + n].setLeftState(true);
        cells[(seedPoint - 1) + n].setRightState(true);

        cells[seedPoint - n].setState(true);
        cells[(seedPoint + 1) - n].setLeftState(true);
        cells[(seedPoint - 1) - n].setRightState(true);
    }

//    cells[seedPoint - 20].setState(true);
//    cells[(seedPoint+1) - 20].setLeftState(true);
//    cells[(seedPoint-1) - 20].setRightState(true);
//
//    cells[seedPoint+1].setState(true);
//    cells[(seedPoint+1+1)].setLeftState(true);
//    cells[(seedPoint-1+1)].setRightState(true);
//
//    cells[seedPoint-1].setState(true);
//    cells[(seedPoint+1-1)].setLeftState(true);
//    cells[(seedPoint-1-1)].setRightState(true);
//
//    cells[seedPoint+2].setState(true);
//    cells[(seedPoint+1+2)].setLeftState(true);
//    cells[(seedPoint-1+2)].setRightState(true);
//
//    cells[seedPoint+3].setState(true);
//    cells[(seedPoint+1+3)].setLeftState(true);
//    cells[(seedPoint-1+3)].setRightState(true);
//
//    cells[seedPoint-3].setState(true);
//    cells[(seedPoint+1-3)].setLeftState(true);
//    cells[(seedPoint-1-3)].setRightState(true);
//
//    cells[seedPoint-2].setState(true);
//    cells[(seedPoint+1-2)].setLeftState(true);
//    cells[(seedPoint-1-2)].setRightState(true);

    // ****NOTE to change initialisation, add different/additional seed points set to true at this point
    // eg.. the following three lines add a second seedpoint next to the first.... 
    //
    // cells[seedPoint+1].setState(true); 
    // cells[(seedPoint+2)].setLeftState(true);     
    // cells[(seedPoint)].setRightState(true);
    // 
    // or more generically.....
    //
    // cells[seedPoint+n].setState(true); 
    // cells[(seedPoint+1+n)].setLeftState(true);     
    // cells[(seedPoint-1+n)].setRightState(true);
    // 
    // where n is the position of the new set point relative to the central seed point. 

  }
  //*******************************************************************************************
  // Method to progress forward 1 generation creating a set of temporary array of next-states
  // then using this to update all cell-states. Note BCs require end neibour states be wrapped  
  //********************************************************************************************

  public void progressGen(){ 

    int i;
    boolean[] store=new boolean[size1D];

    for(i=0;i<size1D;i++) { 
      store[i]=cells[i].nextStateWolfram1D(ruleNumber); 
    }

    for(i=0;i<size1D;    i++)  cells[i].setState(     store[i]  );

    for(i=1;i<size1D;    i++)  cells[i].setLeftState( store[i-1]);

    for(i=0;i<(size1D-1);i++)  cells[i].setRightState(store[i+1]);

    cells[0         ].setLeftState (store[(size1D-1)]);  
    cells[(size1D-1)].setRightState(store[0         ]); 
     
    genNumber++;
  }

  //*************************************** 
  //  Method to pass an array of states.
  //***************************************

  public boolean[] getStates(){
  
    int i;
    boolean[] store=new boolean[size1D];

    for(i=0;i<size1D;i++) { store[i]=cells[i].getState(); }
    
    return(store);

  }

  //Private components:

  Wolfram1DCell cells[];
  int genNumber;
  int size1D;
  int ruleNumber;

}