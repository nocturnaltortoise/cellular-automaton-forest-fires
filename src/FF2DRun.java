
/******************************************************************************* 
 *******************************************************************************
 **                                                                           ** 
 **  This is the top level main program to run John Conways Game of Life      **
 **  Written by Dr Martin Bayley 18/02/11                                     **
 **  In suport of Module COM2005                                              **
 **                                                                           **
 *******************************************************************************
 ******************************************************************************/


import java.awt.*;                           // import the required graphics/swing/io classes
import javax.swing.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileReader;
import java.io.BufferedReader;

                                             // import all our other classes used by this Main Class 
import ca_cells.FF2DCellState;
import ca_grids.FF2DGrid;
import ca_cells.FF2DCell;
import graphics.ImageAxB;


public class FF2DRun extends JPanel{        // The code must extend Jpanel in the declaration

  public void paint(Graphics g) {            // This class describes what the Jpanel should do to paint itself
    Image img = imObj.getImage();            // get an image from the private instance of our bufferedImage control class (imObj) 
    g.drawImage(img, 0,0,this);              // use the Graphics context method .drawImage to put this image onto the JPanel
  }


  public static void main(String[] argv) {   // Standard main (with command line input)

    //***************************************************************
    //* Set up parameters local to running the main....
    //***************************************************************

   
    int xSizeIn=1;             // Game of life x-dimension size of array of cells
    int ySizeIn=1;             // Game of life y-dimension size of array of cells
    int initPercent=50;        // Default initial percent for random initialisation
    int gensIn=-1;             // Initialise number of generations (requires command line input value)
    char readFlag;             // char to test for read based initialisation
    boolean doRead=false;      // flag to trigger read based initialisation

    int i;   // itterator variable
    int j;   // itterator variable
    int k;   // itterator variable

    char checkMe;      // used in the state array input file parser (if selected) to compare string char values
    
    //*************************************************************************************************************** 
    // Parse comand line parameters
    // need to cast a string to an integer and error trap illegal parameter bounds
    // Two possible input command line forms:
    // java FF2DRun (1)(int)xDim (2)(int) yDim (3)(int) % for random initialisation (4)(int) number of generations
    // java FF2DRun (1)(char[0]) = 'r' or 'R'-triggers read based initialisation (2)(int) number of generations
    //***************************************************************************************************************

    if ((argv.length >=2)||(argv.length<=5)) {      // Check for correct number of command line arguments
      if ((argv.length ==3)||(argv.length==5))  {   // If any extra com line string of any type is entered
        writeFlag=true;                             // then set output to "on" - default is "off"
      } 
      try {                                         // use try-catch exception handling.....
        xSizeIn = Integer.parseInt(argv[0]);        // Parse first agument to be an int
        if((xSizeIn>1000)||(xSizeIn<0)){            // If it is possible and is too big report error and stop

          System.err.println("Argument 1 (xSizeIn/Readflag) value must be 0-1000");
          System.exit(1);
        }
      } catch (NumberFormatException e) {           // If parse can not return an integer (as it isn't one)
        readFlag=argv[0].charAt(0);                 // set readFlag to be the first char of the argument
        if((readFlag=='r')||(readFlag=='R')) {      // and test for it being a valid read initialisation argument
	  doRead=true;                              // if so set initialisation type to do read based initialisation
        }
        else{                                       // Report and exit for all other errors on first argument 

          System.err.println("Argument 1(x size of array/ReadFlag) must be an integer or an \"r\" char");
          System.exit(1);
        }
      }
      
      try {                                         // use try-catch exception handling.....                         
        if (doRead==true){                          // If first arg trigger "do read", Parse second agument gensIn to be an int
          gensIn = Integer.parseInt(argv[1]);
          if((gensIn>10000)||(gensIn<0)){           // check for sensible limits if not report and exit
            System.err.println("Argument 2 (Number of generations) must be positive (and less than 10000)");
            System.exit(1);
          }
        } 
        else {                                      // If first arg is not "do read", Parse second agument ySizeIn to be an int
          ySizeIn = Integer.parseInt(argv[1]);
          if((ySizeIn>1000)||(ySizeIn<0)){          // check for sensible limits if not report and exit
            System.err.println("Argument 2 (y size of array) must be positive (and less than 1000)");
            System.exit(1);
          }
        }
      } catch (NumberFormatException e) {           // Report and exit for all other errors on second argument 
        System.err.println("Argument 2(y size of array/number of gens with read flag) must be an integer");
        System.exit(1);
      }
      if (doRead==false){ 
        try {                                       // use try-catch exception handling.....          
          initPercent = Integer.parseInt(argv[2]);
          if((initPercent>100)||(initPercent<0)){   // check for sensible limits if not report and exit
            System.err.println("Argument 3(random initialization %) must be positive (and less than 100) - \"java Run1D\" for help");
            System.exit(1);
          }
        } catch (NumberFormatException e) {         // Report and exit for all other errors on third argument 
          System.err.println("Argument 3(random initialisation %) must be an integer");
          System.exit(1);
        }
        try {                                       // use try-catch exception handling.....          
          gensIn = Integer.parseInt(argv[3]);       // If first arg is not "do read", Parse fourth agument gensIn to be an int
          if((gensIn>10000)||(gensIn<0)){           // check for sensible limits if not report and exit
            System.err.println("Argument 4(Number of generations) must be positive (and less than 10000)");
            System.exit(1);
          }
        } catch (NumberFormatException e) {         // Report and exit for all other errors on forth argument 
          System.err.println("Argument 4(number of generations) must be an integer");
          System.exit(1);
        }
      }
    }
    else {     // A bit of command line help if wrong number of entered
      System.out.println("Input Parameters Help:\n\n two way to run: -\n\n(A)******** java FF2DRun (int){1} (int){2} (int){3} (int){4}\n\n");
      System.out.println("1 -x dimension of array\n2 -y dimension of array\n3 -Percent set initialisation\n4 -No. of generations\n\n");
      System.out.println("(B)******** java FF2DRun (char){1} (int){2}\n\n1 -\'r\' or \'R\',read \"FF_input_state_array.txt\" infile for initialisation.\n2 -No. of generations.");
      System.exit(1);
    }

    //***********************************************************************
    //  If read based initialisation is requested in the command line 
    //   - first read to find the dimensions of the CA cell array 
    //***********************************************************************

    if(doRead==true) {                                // if "do read" command line input is used
      System.out.println("Attempting to obtain cell array size from file \"FF_input_state_array.txt\"");
      try {                                           // use try-catch exception handling
        BufferedReader inarray = new BufferedReader(new FileReader("FF_input_state_array.txt")); // open the input file as a BufferedReader
        String strIn;                                 // a string to hold a line of text input
        boolean firstTimeFlag=true;                   // a flag to set xSizeIn from the first line of the input text file
        int linecount=0;                              // a line count to set the ySizeIn from the number of lines in the input text file 
        while ((strIn = inarray.readLine()) != null) {                // While the file can still be read (cos it's not reached the end)...
          if (firstTimeFlag==true){                                   // for first line set xSizeIn
            xSizeIn=strIn.length();                                                       
            System.out.println(" X dimension found to be "+xSizeIn);  // tell the world xDim
            firstTimeFlag=false;
          }
          linecount++;                                                 //increment linecount
        }  
        ySizeIn=linecount;                                             // set ySizeIn when no more lines can be read (i.e. dropped out of while)
        System.out.println(" Y dimension found to be "+ySizeIn);       // tell the world yDim
        inarray.close();                                               // close the BufferedReader file object
      } catch (IOException e) {                                        // If file can not be opened as a BufferdReader report and exit
          System.err.println("Error reading state input array for cell array sizing");
          System.exit(1);
      }
    }
    //****************************************************************************
    //  Initialize key objects/variables based on CA cell array dimension values 
    //****************************************************************************

    boolean[][] store = new boolean[xSizeIn][ySizeIn];   // local used to store cell array states
    FF2DCellState[][] colourStore = new FF2DCellState[ySizeIn][xSizeIn];
    for (int row=0; row<ySizeIn; row++) {
      for (int col=0; col<xSizeIn; col++) {
        colourStore[row][col] = new FF2DCellState();
      }
    }

    FF2DGrid cellSet = new FF2DGrid(xSizeIn,ySizeIn);  // call constructor for the FF2DGrid class object now we know dimensions
  
    //***********************************************************************
    //  If read based initialisation is requested in the command line 
    //   - second read to set the states of the CA cell array 
    //***********************************************************************
  
    if (doRead==true){                                // if "do read" command line input is used
      try {                                           // use try-catch exception handling
        BufferedReader inarray = new BufferedReader(new FileReader("FF_input_state_array.txt"));  // open the input file again
        String strIn;                                 // a string to hold a line of text input
        int linecount=0;                              // a line count to set the ySizeIn from the number of lines in the input text file 
        while ((strIn = inarray.readLine()) != null) {                // While the file can still be read (cos it's not reached the end)...
          for(i=0;i<strIn.length();i++){                              // itterate trough each char to compare
            checkMe=strIn.charAt(i);                                  // copy a particular char from the string 
            if(checkMe=='1') store[i][linecount]=true;                // if it's a "1" then set that position to true in the store state array
          }
          linecount++;                                                // itterate linecount  
        }
        inarray.close();                                              // close the file
      } catch (IOException e) {                                       // If file can not be opened as a BufferdReader report and exit
          System.err.println("Error reading state input array for initialisation");
          System.exit(1);
      }

    //**************************************************************************************************    
    // If read initialisation: call grid method to initialise, passing in store state array of booleans
    //**************************************************************************************************
 
      cellSet.setGridPassed(store);  // member function to pass in a state array

    //**************************************************************    
    // Else initialise using random percentage based initialisation
    //**************************************************************

    } else {

      cellSet.setGridRandom(initPercent);  // member function for random initialisation
    }

    /**********************************/
    /*  create Graphic output frame   */
    /**********************************/

    JFrame frame = new JFrame();                                          // call constructor for a new JFrame container
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                 // set standard close funtionality
    frame.setSize(1024,768);                                              // set initial Frame size - this will change!
    frame.setTitle("Forest Fires Cellular Automata");        // set Title of Frame
    frame.setVisible(true);                                               // set visible (it appears!)
    frame.getContentPane().add(new FF2DRun());                           // tell the frame to add the contentPane (this actions the
                                                                          // paint function described at the top of this main class)

    Insets insets = frame.getInsets();                // initialise an Insets object to hold the size of the bourders of our frame
    int insetwidth = insets.left + insets.right;      // find the additional bourder width
    int insetheight = insets.top + insets.bottom;     // find the additional bourder height

    imObj.setUnits(xSizeIn,ySizeIn);         // call a member function of the imObj object to set its image scaling graphics variables
    int[] brickSize = imObj.getUnits();      // call a member function of the imObj object to find the pixel dims for graphic -  
                                             // rendering of the CA cell state array

    frame.setSize(((brickSize[0]*xSizeIn)+insetwidth),((brickSize[1]*ySizeIn)+insetheight));   // Reset the JFrame to the correct size 

    try {                                           // try-catch Exception handling to open an output textfile
      PrintStream outtext = new PrintStream(new FileOutputStream("forestFireOutput.txt")); // open a new PrintStream text file output object
    
      for(i=0;i<=gensIn;i++){                       // itterate through generations

        store = cellSet.getStates();                // get the local state array
        colourStore = cellSet.getFF2DStates();
//        imObj.drawCells(store);                     // call a member function of the imObj object to redraw the graphic image using the stored state array
        imObj.drawCells(colourStore);
        frame.getContentPane().repaint();           // call a Jframe member function to repaint the contenPane
        cellSet.printFireStatistics();
        cellSet.writeFireStatistics();

        try {                                           // try-catch Exception handling to implement a pause
          Thread.currentThread().sleep(100);             // Sleep 1000= 1 second
        }
        catch (InterruptedException e) {                // if error trace
          e.printStackTrace();
        }

        //************************************
        // Output Images  
        //************************************
       
        if(writeFlag==true){
          imObj.writeImage("OutputImages",("forestFireImage"+i+".png")); // call a member function of the imObj object to output an image                                              
        }     
        imObj.blanc();                  // blanc the image in prep for the next drawCells function call

        //***********************************
        // Output Text File record
        //***********************************
        if(writeFlag==true){
          for (j=0;j< ySizeIn;j++){           // This code uses _ and | chars to draw a box around the output state arrays to
            if(j==0){                         //    - separate different generational output 
              outtext.format(" "); 
              for (k=0;k< xSizeIn+2;k++){ 
                outtext.format("-"); 
              } 
              outtext.format("\n");
            }
            outtext.format(" |"); 
            for (k=0;k< xSizeIn;k++){ 
              outtext.format("%c", ((char)(store[k][j]?'O':' ')));    // this is where the chars for true and false are set for the output text file
            }                                                         //    - currently 'O'=true ' ' or space = false 
            outtext.format("|\n");
          }
        }
        //******************************************
        // progress to the next generation
        //******************************************

        if(i!=gensIn){ cellSet.progressGen(); }
      }
      outtext.close();                     // close the output text file
      cellSet.closeFile();

    } catch (FileNotFoundException e) {     // if output text file could not be opened, report and exit.
      System.err.println("can not open output file");
      System.exit(1);
    }

  }
  private static boolean writeFlag = false;                 //  This is set true to allow image and textfile write...
  private static ImageAxB imObj = new ImageAxB(1024,768);   //  This stores a private instance of an ImageAXB object..
}                                                           //  this is accessed by in this main class to be repainted onto 
                                                            //  the contentpane of the JFrame container as well as having 
                                                            //  member functions available to redraw, blanc and output to file etc. 
