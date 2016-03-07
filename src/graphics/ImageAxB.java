//********************************************************************************
//**  Graphic Class to setup a grapic BufferedImage to visualise CA state arrays  
//**    Dr Martin Bayley 18/02/11
//**    Module COM2005
//*********************************************************************************

package graphics;                     // declare class to be in the graphics package (implications for class dir. structure)

import ca_cells.FF2DCell;
import ca_cells.FF2DCellState;

import java.awt.Graphics;             // import all supporting java graphics and io classes
import java.awt.Image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO; 
import java.io.File;
import java.io.IOException;

public class ImageAxB{                                       // declare class
    
  /******************************************************/
  /* Constructors - blank image                         */ 
  /******************************************************/

  public ImageAxB(int aIn,int bIn) {
    xDim=aIn;                                  // image constructor sets x dimension
    yDim=bIn;                                  // image constructor sets y dimension
    imUnit = new int[2];                       // create 1x2 array for brick x,y, dims in pixels
    cellDims = new int[2];                     // create 1x2 array for CA cell array dims 
    cellDims[0]=0; cellDims[1]=0;              // initialise cell array dims to 0    

    myBufferedImage = new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
  }
 
  //**********************************************************************
  // drawCells Memeber function- takes a boolean CA state array and 
  // draws "true" state rectangles on a graphics context of the classes 
  // BufferedImage object.   
  //**********************************************************************

  public void drawCells(boolean[][] statesIn){

    int i;    int j;  // itterators 
       
    Graphics g = myBufferedImage.getGraphics();    // create a graphics context of the BufferedImage object

    for (i=0 ;i<statesIn[0].length;i++){           // for each true CA state array position.... 
      for(j=0;j<statesIn.length   ;j++){
        if (statesIn[j][i]==true) {                // draw an appropriatly positioned and sized rectangle
          g.fill3DRect(((j*imUnit[0])),((i*imUnit[1])),(imUnit[0]),(imUnit[1]),true);
        }
      }
    }
  }

  public void drawCells(FF2DCellState[][] statesIn) {
    int i;    int j;  // itterators

    Graphics g = myBufferedImage.getGraphics();    // create a graphics context of the BufferedImage object

    for (i=0 ;i<statesIn[0].length;i++){           // for each true CA state array position....
      for(j=0;j<statesIn.length   ;j++){
        g.setColor(statesIn[j][i].getColour());
        g.fill3DRect(((j*imUnit[0])),((i*imUnit[1])),(imUnit[0]),(imUnit[1]),true);
      }
    }
  }

  //**********************************************************************
  // drawCells Memeber function- takes a boolean CA state array and 
  // draws "true" state rectangles on a graphics context of the classes 
  // BufferedImage object.   
  //**********************************************************************

  public void drawCellsColour(boolean[][] statesIn,int colourflag){

    int i;    int j;  // itterators 

    Graphics g = myBufferedImage.getGraphics();    // create a graphics context of the BufferedImage object
    
    if (colourflag==0) g.setColor (Color.gray);
    if (colourflag==1) g.setColor (Color.orange);
    if (colourflag==2) g.setColor (Color.red);
    // could add a whole hot to cold palet.... 

    for (i=0 ;i<statesIn[0].length;i++){           // for each true CA state array position.... 
      for(j=0;j<statesIn.length   ;j++){
        if (statesIn[j][i]==true) {                // draw an appropriatly positioned and sized rectangle       
          g.fill3DRect(((j*imUnit[0])),((i*imUnit[1])),(imUnit[0]),(imUnit[1]),true);
        }
      }
    }
  }  

  //******************************************************************************  
  // member function to return the image (for use in the Jpanel paint function)
  //******************************************************************************

  public Image getImage(){ return (myBufferedImage); } 

  //*********************************************** 
  // member function to write out an image file 
  //***********************************************

  public void writeImage(String pathname, String filename){      
    try {                                                        // use try-catch exception handling for file io error
      File outputfile = new File(pathname,filename);             // open the file
      if (cellDims[0]==0){                                       // if writing before CA cell array dims are set - write whole image
        ImageIO.write(myBufferedImage, "png", outputfile); 
      } else {
      BufferedImage tempBufferedImage;                           // else crop it to exaclty the right size based on brick size x cell array size
        tempBufferedImage=myBufferedImage.getSubimage(0,0,(cellDims[0]*imUnit[0]),(cellDims[1]*imUnit[1]));        
        ImageIO.write(tempBufferedImage, "png", outputfile);
      }
    } catch (IOException e) {                                    // if file can not be created, report and exit 
      System.err.println("Image file could not be written");
      System.exit(1);
    }
  }

  //***********************************************
  // member function to blanc the image object 
  //***********************************************

  public void blanc(){                             
    Graphics g = myBufferedImage.getGraphics();    // creates a graphics context of the BufferedImage object
    g.clearRect(0,0,xDim,yDim);                    // uses a graphics context member function to blanc a big rectangle from the BufferedImage
  }

  //**************************************************************************
  // member functions to set (and return) CA array and graphics variables 
  //**************************************************************************

  public void setUnits(int xCells, int yCells){    // This member function is called after the size of the CA state cell array is known 
                                                   // it sets cellDims[] and also the imUnit[] brick size. This is then able to be accessed 
    cellDims[0]=xCells;                            // by the getUnits member function (below) to enable the JFrame to correctly rezize
    cellDims[1]=yCells;                            // and by the writeImage member function to crop the correct size image before writing to file.
    imUnit[0] =(int)(xDim/xCells);                  
    imUnit[1] =(int)(yDim/yCells);
  }
  public int[] getUnits(){ 
    return(imUnit);
  }

  //***********************************************************************
  // Private class objects and variables
  //***********************************************************************

  private BufferedImage myBufferedImage;    // the bufferedImage object
  private int xDim=100;                     // default x sixe of image
  private int yDim=100;                     // default y size of image
  private int[] imUnit;                     // for drawing of cells - brick size (x,y) in pixels
  private int[] cellDims;                   // dimensions (x,y) of the cell state array. 

}