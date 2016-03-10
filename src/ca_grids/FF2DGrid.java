//***************************************************************************************************** 
//* Grid Class - this is the second hierachy of classe and defines the basic orgaisation of cells
//* initialises them (both in the constructor)and then controls the generational progession. 
//* It also implements a boundary condition (in this case we assume periodic (wrapping) BCs)
//* A method to pass an array of current states allows the Main program to manage output...  
//**    Dr Martin Bayley 18/02/11
//**    Module COM2005  
//***************************************************************************************************

package ca_grids;                  // assign to the ca_grids package (implications for class dir. structure)

import ca_cells.FF2DCell;         // this class uses the lower level cell class to make an array of cell objects
import ca_cells.FF2DCellState;

import java.io.FileWriter;
import java.util.Random;


public class FF2DGrid{

  private Random randomGenerator = new Random(System.currentTimeMillis());
  private FileWriter outputFile = null;
  // seedpoints represented as int [seedPoint] [x,y,fuelLevel]

  public static int seedPoints[][] = new int[1000][3];

  //***************************************************************************************
  // Constructor - this initialises an array of cells
  //***************************************************************************************

  public FF2DGrid(int xLen, int yLen) {    // constructor has cell array dimms passed in
                                            // in then initialises all private variables 

    size2D[0]=xLen;  // FF2DGrid object stores x dimension of cell array
    size2D[1]=yLen;  // FF2DGrid object stores y dimension of cell array
    genNumber =0;    // genNumber initially set to 0

    createSeedPoints(randomGenerator, size2D, maxFuelLevel);

    try {
      outputFile = new FileWriter("graph.csv");
    }
    catch (java.io.IOException e) {
      System.out.println("Couldn't open output file for writing (" + e + ").");
    }

    cells= new FF2DCell[size2D[0]][size2D[1]];     // initialise the array of cell objects
    for(int i=0;i<size2D[0];i++){
      for(int j=0;j<size2D[1];j++)  {
        cells[i][j]=new FF2DCell(i,j);                // call the constructor for each instance of the cell class
      }  
    }

    // Make sure that seedpoints have correct values in their FF2DCell representation
    rectifySeedpointCells(cells);
  }

  // Set up the FF2DCellStates in accordance to initial boolean states
  public void setupStates() {
    for (int i=0; i<size2D[0]; i++) {
      for (int j=0; j<size2D[1]; j++) {
        if (cells[i][j].getState()) {
          cells[i][j].setCellState(new FF2DCellState(FF2DCellState.CELL_STATE.ALIVE));
        }
      }
    }
  }

  public static int[] findNearestSeedPoint(int x, int y, int[][] seedPoints){

    double bestDistance = 100000;
    int[] bestPoint = new int [3];

    for(int[] point : seedPoints){
      int xDist = Math.abs(x - point[0]);
      int yDist = Math.abs(y - point[1]);
      double euclideanDist = Math.sqrt(Math.pow((double)xDist, 2) + Math.pow((double)yDist, 2));
      if(euclideanDist < bestDistance){
        bestDistance = euclideanDist;
        bestPoint = point;
      }
    }

    return bestPoint;
  }

  private static void createSeedPoints(Random randomGenerator, int[] size2D, int maxFuelLevel){
    seedPoints[0][0] = randomGenerator.nextInt(size2D[0]);
    seedPoints[0][1] = randomGenerator.nextInt(size2D[1]);
    seedPoints[0][2] = randomGenerator.nextInt(maxFuelLevel);

    for(int i = 0; i < seedPoints.length; i++){
      seedPoints[i] = new int [3];
      seedPoints[i][0] = randomGenerator.nextInt(size2D[0]);
      seedPoints[i][1] = randomGenerator.nextInt(size2D[1]);
      if(i % 3 == 0){
        seedPoints[i][2] = 0;
      }else{
        seedPoints[i][2] = randomGenerator.nextInt(maxFuelLevel);
      }
    }

  }

  public static void rectifySeedpointCells(FF2DCell[][] cells) {
    for(int[] point : seedPoints) {
      int x = point[0];
      int y = point[1];
      int fuelLevel = point[2];
      cells[x][y] = new FF2DCell(x, y);
      cells[x][y].setFuelLevel(fuelLevel);
      cells[x][y].setCellState(new FF2DCellState(FF2DCellState.CELL_STATE.DEAD));
    }
  }


  //******************************************************************************************************************
  // Random initialisation using a %set threshold with a random number generator to choose initial state for each bit
  //******************************************************************************************************************
  
  public void setGridRandom(int percentIn){  

    double randomNum;      // local stores a random number from Math.random() function
        
    for(int i=0;i<size2D[0];i++){
      for(int j=0;j<size2D[1];j++){
        randomNum=100 * Math.random();

	if(randomNum<percentIn) {         // if the random number is less than the threshold passed in ...
          cells[i][j].setState(1,1,true); // set the cell state array center point (the state of the cell) as true
        }
      }
    }
    setNeighborStates();     // after all array points have been subjected to the random initialisation process
  }                          // call the private setNeighbourState member function to reset all cell state 3x3 
                             // arrays with any new neighbour information (including boundary wrapping).


  //**********************************************************************************************************************
  // Initialisation of the array - simple passed in array (must be the same size as the x and y dims of the constructor) 
  //**********************************************************************************************************************
  
  public void setGridPassed(boolean setVals[][])  {    // pass in a 2d array of booleans (set states)

    for(int i=0;i<size2D[0];i++){                       // for each cell array......
      for(int j=0;j<size2D[1];j++)  {
        cells[i][j].setState(1,1,setVals[i][j]);    // set the cell state array center point (the state of the cell)
      }                                             // to the state of the coresponding position in the passed-in array 
    }
    setNeighborStates(); // after all array points have been subjected to the random initialisation process
  }                      // call the private setNeighbourState member function to reset all cell state 3x3 
                         // arrays with any new neighbour information (including boundary wrapping).



  //************************************************************************************************************
  // Method to progress forward 1 generation creating a temporary array of next-states
  // then using this to update all cell centre -states. 
  //************************************************************************************************************
  
  // *Note - this could be achieved in a single stage without the local state storage and which would be faster
  //         however, doing it like this avoids using confusing nested object.method calls and exposes the 
  //         next State data (in store) for debugging....


  public void progressGen(){

    boolean[][] store=new boolean[size2D[0]][size2D[1]];  // stores a local next state array

    for(int i=0;i<size2D[0];i++) {                            // for each cell array position
      for(int j=0;j<size2D[1];j++) {

        // If fuel level reaches 0 and not already inRefactoryCycle, startRefactory
        if (cells[i][j].getFuelLevel() == 0 && !cells[i][j].inRefractoryCycle()) {
            cells[i][j].startRefractory();
        }

        // If cell is not dead, reduce fuel level to simulate burning
        //&& (genNumber % fuelDecrementInterval == 0)
        if (cells[i][j].getState()) {
          cells[i][j].decrementFuelLevel();
        }
        store[i][j]=cells[i][j].nextState();              // copy next state to the store array
      }
    }

    for(int i=0;i<size2D[0];i++) {                           // for each cell array position
      for(int j=0;j<size2D[1];j++) {
        cells[i][j].setState(1,1,store[i][j]);           // Use the store array to set all next states 
      }
    }

    setNeighborStates(); // after all array points have been subjected to the random initialisation process
                         // call the private setNeighbourState member function to reset all cell state 3x3 
                         // arrays with any new neighbour information (including boundary wrapping).
    genNumber++;         // +1 to genNumber to count number of generations completed 
  }

  //******************************************************************************** 
  //  Method to allow this class to pass out its array of states.
  //   - this passes an array of the cell states up to the main program 
  //********************************************************************************

  public boolean[][] getStates(){

    boolean[][] store=new boolean[size2D[0]][size2D[1]];  // local array of states 

    for(int i=0;i<size2D[0];i++) {
      for(int j=0;j<size2D[1];j++) { store[i][j]=cells[i][j].getState(); }   // for all array positions copy array staes to the local state array
    }
    return(store);       // return the local stae array store

  }

  public FF2DCellState[][] getFF2DStates() {
    FF2DCellState[][] store=new FF2DCellState[size2D[0]][size2D[1]];  // local array of states

    for(int i=0;i<size2D[0];i++) {
      for(int j=0;j<size2D[1];j++) { store[i][j]=cells[i][j].getCellState(); }   // for all array positions copy array staes to the local state array
    }
    return(store);
  }
  //***************************
  //Private components:
  //****************************


  private FF2DCell cells[][];            // declare the array of cells (of type FF2DCell-previously imported)
  private int genNumber;                  // a private to store a count of generations
  private int[] size2D= new int[2];       // a int vector of the cell array dimensions 



  //*****************************************************************************************************************************
  // Neighborhood setting private member function for general use by all member functions that need to do this 
  //   - following any initialisation process and after generational progression to update all neighbour info..
  //   This function also implements boundary conditions (through neighbor setting) so different BCs could be implented in here...  
  //*****************************************************************************************************************************
  
  private void setNeighborStates(){
  
  int i; int j; int k; int l;    //increment counters

  boolean[][] nMap = new boolean[size2D[0]+2][size2D[1]+2];   // a local array of states - this is 2 bigger than the cell array
                                                              // in both dimensions to include a wrapped +1 border 

    for(i=0;i<size2D[0];i++){                         // set all cell states in local array (offset in nMap by +1)
      for(j=0;j<size2D[1];j++)  { 
        nMap[i+1][j+1]= cells[i][j].getState();       // sets to the (1,1) state value of the cells 3x3 state array (which includes neighbours) 
      }
    }
//    for(i=0;i<size2D[0];i++){
//      nMap[i+1][0]          = cells[i][size2D[1]-1].getState();   // set y =-1 and y= max+1 local array wrapping boundary states
//      nMap[i+1][size2D[1]+1]= cells[i][0          ].getState();   //
//
//    }
//    for(i=0;i<size2D[1];i++){
//      nMap[0          ][i+1]= cells[size2D[0]-1][i].getState();   // set x =-1 and x= max+1 local array wrapping boundary states
//      nMap[size2D[0]+1][i+1]= cells[0          ][i].getState();
//    }
//
//    nMap[0          ][0          ]= cells[size2D[0]-1][size2D[1]-1].getState();  // set local array corner wrapping boundary states
//    nMap[size2D[0]+1][size2D[1]+1]= cells[0          ][0          ].getState();
//    nMap[size2D[0]+1][0          ]= cells[0          ][size2D[1]-1].getState();
//    nMap[0          ][size2D[1]+1]= cells[size2D[0]-1][0          ].getState();

/*    for absorbant BCs (all edges set to false)....

    for(i=0;i<size2D[0];i++){ nMap[i+1][0  ]= false; nMap[i+1        ][size2D[1]+1]= false; }
    for(i=0;i<size2D[1];i++){ nMap[0  ][i+1]= false; nMap[size2D[0]+1][i+1        ]= false; }

    nMap[0          ][0          ]= false;  // set local array corner wrapping boundary states
    nMap[size2D[0]+1][size2D[1]+1]= false;
    nMap[size2D[0]+1][0          ]= false;
    nMap[0          ][size2D[1]+1]= false;

*/



    for(i=0;i<size2D[0];i++){                  // fill all cell 3x3 state arrays
      for(j=0;j<size2D[1];j++){ 
        for(k=0;k<3;k++){
          for(l=0;l<3;l++)  { 
            cells[i][j].setState(k,l,nMap[i+k][j+l]);
          }
        }
      } 
    }
  }



  // Compute number of cells currently on fire
  private int computeCellsOnFire() {
    int onFire = 0;

    for (int i=0; i<size2D[0]; i++) {
      for (int j=0; j<size2D[1]; j++) {
        if (cells[i][j].getState()) {
          onFire ++;
        }
      }
    }

    return onFire;
  }


  // Calculates burning statistics and prints them on screen
  public void printFireStatistics() {
    int total = size2D[0] * size2D[1];
    int onFire = computeCellsOnFire();
    double percent = ((double)onFire/(double)total)*100.0;
    System.out.printf("%di: There are %d/%d cells on fire (%% %f)\n", genNumber, onFire, total, percent);
  }

  // Writes proportion of cells on fire at each iteration to CSV file
  public void writeFireStatistics() {
    int total = size2D[0] * size2D[1];
    int onFire = computeCellsOnFire();

    String output = genNumber + "," + onFire + "," + total + "\n";
    try {
      outputFile.write(output);
    }
    catch (java.io.IOException e) {
      System.out.println("Couldn't write to file (" + e + ").");
    }

  }

  public void closeFile() {
    try {
      outputFile.close();
    }
    catch (java.io.IOException e) {
      System.out.println("Couldn't close file (" + e + ")");
    }
  }

  // Decrement fuel every n turns
  private int maxFuelLevel = 30;
  private int fuelDecrementInterval = 1;
}