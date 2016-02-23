/**********************************
 * 
 * Wolfram 1D CA - a Cell object 
 *
 **********************************/

package ca_cells;

public class Wolfram1DCell{

  //*********************************************************************  
  //  Constructor - initialise all state varibles  to false (zero) state 
  //*********************************************************************

  public Wolfram1DCell(){
    cellState=false;
    leftState=false;
    rightState=false;
  }
  
  //******************************************************************************************
  //  Member functions to allow the Generation class (next level up-defining a group of cells)
  //  to set up the private cell-state variables for the neighborhood and report its own state 
  //******************************************************************************************

  public void setState(boolean set){
    cellState=set;
  }
  public void setLeftState(boolean set){
    leftState=set;
  }
  public void setRightState(boolean set){
    rightState=set;
  }
  public boolean getState(){
    return(cellState);
  }

  //****************************************************************************
  //  Create a next-state method to encapsulate Wolframs rules and return the 
  //  next generational state for that cell.
  //****************************************************************************

  public boolean nextStateWolfram1D(int ruleNumber){


    boolean[] ruleBits={false,false,false,false,false,false,false,false};
    int[]     bitVals ={1, 2, 4, 8, 16,32,64,128};
    int bitOperate=ruleNumber;
    int i;
    int cellRulePos; 


    //  Parse ruleNumber to obtain bit values 
    //  (big to small bit subtractive decomposition) 

    for(i=7;i>=0;i--){
      if (bitOperate>=bitVals[i]) { ruleBits[i]=true; bitOperate=bitOperate-bitVals[i]; }
    }

    //  Find cellRulePos as the 3-bit score of this cells states 
    //  (left(bit4)-cell(bit2)-right(bit1)) 
    //  eg. false,true,false = 0+2+0 = 2    true,false,true =4+0+1 = 5....
    //  Note (int)(blah?1:0) casts a boolean to an int of value 1(true) or 0(false) 

    cellRulePos=(4*((int)( leftState?1:0)))
               +(2*((int)( cellState?1:0)))
               +   ((int)(rightState?1:0)) ;

    return(ruleBits[cellRulePos]);	

  }

  //***********************************************************************
  // Create a method to pass back the bit pattern for the cell rule
  //***********************************************************************

  public boolean[] getRuleBits(int ruleNumber){

    boolean[] ruleBits={false,false,false,false,false,false,false,false};
    int[]     bitVals ={1, 2, 4, 8, 16,32,64,128};
    int bitOperate=ruleNumber;
    int i;

    for(i=7;i>=0;i--){
      if (bitOperate>=bitVals[i]) { ruleBits[i]=true; bitOperate=bitOperate-bitVals[i]; }
    }
    return(ruleBits);
  }


  //******************************************************************************
  //private components - stores states of cell and all other in its neighborhood
  //******************************************************************************

  private boolean cellState;
  private boolean leftState;
  private boolean rightState;
 
}