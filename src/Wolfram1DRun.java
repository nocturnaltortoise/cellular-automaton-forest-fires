
/******************************************************************** 
 * 
 * This is the top level main program to run wolframs 1d CA
 *
 *********************************************************************/

import java.awt.*;                           // import the required graphics/swing/io classes
import javax.swing.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import ca_grids.Wolfram1DGrid;
import ca_cells.Wolfram1DCell;
import graphics.ImageAxB;

public class Wolfram1DRun extends JPanel{
  public void paint(Graphics g) {            // This class describes what the Jpanel should do to paint itself
    Image img = imObj.getImage();            // get an image from the private instance of our bufferedImage control class (imObj) 
    g.drawImage(img, 0,0,this);              // use the Graphics context method .drawImage to put this image onto the JPanel
  }

  public static void main(String[] argv) {

   //***************************************************************
   //* set up parameters ruleNumber, size1D and numberOfGenerations
   //***************************************************************

    //**************************************************************************** 
    // Parse comand line parameters
    // need to cast a string to an integer and error trap illegal paramter bounds
    // eg. rule number must be a 0-255 integer
    //****************************************************************************

    int ruleIn=-1;
    int gensIn=-1;

    int i;
    int j;
    

    if (argv.length ==2) {
      try {
        ruleIn = Integer.parseInt(argv[0]);
        if((ruleIn>255)||(ruleIn<0)){
          System.err.println("Argument 1(Rule Number) must be between 0-255 - \"java Wolfram1DRun\" for help");
          System.exit(1);
        }
      } catch (NumberFormatException e) {
        System.err.println("Argument 1(Rule Number) must be an integer - \"java Wolfram1DRun\" for help");
        System.exit(1);
      }
      
      try {
        gensIn = Integer.parseInt(argv[1]);
        if((gensIn>511)||(gensIn<0)){
          System.err.println("Argument 2 (Number of Generations) must be positive and less than 511 to be displayed - \"java Wolfram1DRun\" for help");
          System.exit(1);
        }
      } catch (NumberFormatException e) {
        System.err.println("Argument 2(1D array size) must be an integer - \"java Wolfram1DRun\" for help");
        System.exit(1);
      }
    }
    else { 
      System.out.println("Input Parameters Help:\n\nRun1D (int){1} (int){2}\n\n1 -Wolfram rule number\n2 -Number of generations\n");
      System.exit(1);
    }


    //***************************************************************
    // not three command line inputs triggers some help output text
    //***************************************************************

    System.out.println("Wolfram Rule Number = "+ruleIn+"\nNumber of Generations="+gensIn+"\n");
     
    int arrayWidth=(2*gensIn)+1;
    int arrayHeight= gensIn+1;

    boolean[]   store  = new boolean[arrayWidth];
    boolean[][] imArray= new boolean[arrayWidth][arrayHeight];

    Wolfram1DCell testCell= new Wolfram1DCell();
    boolean bits[]=testCell.getRuleBits(ruleIn);

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1024,512);
    frame.setTitle("Wolfram 1D Cellular Automata");
    frame.setVisible(true);
    frame.getContentPane().add(new Wolfram1DRun());

    Insets insets = frame.getInsets();
    int insetwidth = insets.left + insets.right;
    int insetheight = insets.top + insets.bottom;

    imObj.setUnits(arrayWidth,arrayHeight);
    int[] brickSize = imObj.getUnits();

    frame.setSize(((brickSize[0]*arrayWidth)+insetwidth),((brickSize[1]*arrayHeight)+insetheight));

    Wolfram1DGrid cellSet = new Wolfram1DGrid( ruleIn , arrayWidth );

    try {

      PrintStream outtext = new PrintStream(new FileOutputStream("Wolfram1DOutput.txt"));

      outtext.format("Bits   128 64  32  16  8   4   2   1\n");
      outtext.format("Values ");
      for (i=7;i>= 0;i--){ 
         outtext.format("%-3c ", ((char)(bits[i]?'1':'0'))); 
      } 

      for(i=0;i<=gensIn;i++){
        store = cellSet.getStates();
        outtext.format("\n|");
        for (j=0;j< arrayWidth;j++){ 
           imArray[j][i]=store[j];
           outtext.format("%c", ((char)(store[j]?'O':' '))); 
        } 
        outtext.format("|");
        imObj.drawCells(imArray);
        frame.getContentPane().repaint();

        cellSet.progressGen();
      }
      outtext.close();
      imObj.writeImage("OutputImages",("Wolfram1D_Rule"+ruleIn+".png"));

    } catch (FileNotFoundException e) {
      System.err.println("can not open output file");
      System.exit(1);
    }
  }

  private static ImageAxB imObj = new ImageAxB(1024,512);
}    
