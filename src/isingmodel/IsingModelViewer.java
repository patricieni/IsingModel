/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package isingmodel;

import java.awt.Graphics;
import javax.swing.JComponent;
import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
/**
 *
 * @author patric
 */
public class IsingModelViewer extends JComponent {
 
   
 // Field to hold the state of the ising model that we want to view 
    private final IsingLattice ising;
   // private BufferedImage bufferedImage;
  //  private Graphics2D bufferedGraphics;
    
    
 // Constructor   
    public IsingModelViewer(IsingLattice isingView){
    ising = isingView;
    setOpaque(true);
    setBackground(Color.WHITE);
    }
    
 // Painting methods. Need to create squares that are as big as the whole frame divided by the dimension
    private void drawRedSquare(Graphics g, int cx, int cy, int wh){
            // red is 1 
            g.setColor(Color.RED);
            g.fillRect(cx, cy, wh, wh);
        }
    private void drawGreenSquare(Graphics g, int cx, int cy, int wh){
            // red is 1 
            g.setColor(Color.GREEN);
            g.fillRect(cx, cy, wh, wh);
    }
    private void drawBlackSquare(Graphics g, int cx, int cy, int wh){
            // black is 1
            g.setColor(Color.BLACK);
            g.fillRect(cx, cy, wh, wh);
        }
   // private void drawData(int width, int height ){
           
    //    bufferedImage = new BufferedImage(ising.getDimension(),ising.getDimension(),
     //   BufferedImage.TYPE_INT_ARGB);
      //  bufferedGraphics = bufferedImage.createGraphics();
       
   // }    
        
    
@Override
	protected void paintComponent(Graphics g) {
	     // First, get the dimensions of the component
		int width = getWidth(), height = getHeight();

	     // Clear the background if we are an opaque component
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
		}
                
             // Fix the case when it's smaller than 1 or not integer? Cannot do that..
                double dimScaled = width/ising.getDimension();
                //int x1=0,y1 = 0;
          for (int x = 0,x1=0; x < ising.getDimension(); x++,x1+=dimScaled) {
             for (int y = 0,y1=0; y < ising.getDimension(); y++,y1+=dimScaled) {
                    if (dimScaled<1);// you need to deal with this somehow;
                    else {
                        if (ising.getLattice()[x][y] == -1){    
                            this.drawGreenSquare(g, x1, y1,(int)dimScaled);
                            //y1=y1+3;
                         }
                   else { 
                    this.drawRedSquare(g, x1, y1,(int)dimScaled); 
                   }
                    }
             }
           }        
         // MORE information here
                
                
                
	}
        
 // More methods here
        
        
        
        
            
}
