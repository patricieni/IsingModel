/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isingmodel;

import java.util.Random;
//import java.util.Scanner;

/**2
 *
 * @author patric
 */
public class IsingLattice {

    private static boolean isNeighbour(int x1, int y1, int x2, int y2) {
        return (Math.pow(10,-3)>Math.abs(1 - Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2))));
        // check for smaller than 0.001 to eliminate errors
    }
    
    
    private final int m;
    private final int [][] lattice; 
    public IsingLattice(int dim)
    {
        m = dim;
        lattice = new int[m][m];
        generateConfig();
    }
    // Copy Constructor for testing purposes
    public IsingLattice(int[][] lattice1, int dimension) {
         this.lattice = lattice1;
         this.m = dimension;
    }
    
    public void initializeLattice(){
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                this.lattice[i][j] = +1;
            }
        }
    }
  
    private void generateConfig()       
    {
            // Build  a random configuration of spin up-down
        Random randomSpin = new Random();
        //System.out.println("Generated lattice looks like this:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (randomSpin.nextDouble() < 0.5) {
                    lattice[i][j] = -1; 
                } else {
                    lattice[i][j] = 1;
                }
          //      System.out.printf("%d ", lattice[i][j]);
            }
          //  System.out.println();

        }
     }
    public int [][] getLattice(){
    
    return this.lattice;
    }
    
    public int getDimension(){
    return this.m;
    }
    
    
    public void doFlip(int x, int y)
    {
        lattice[x][y] = - lattice[x][y];
    }
    
 // Easy to print version for the lattice configuration
    public void printLattice() {
        System.out.println("New configuration looks like this:\n");

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%d ", lattice[i][j]);
            }
            System.out.println();
        }
    }
    
 // Calculate the energy difference if a spin is randomly flipped  
    public int calculateEnergyDiff(int x,int y) {
        int energy = 0;
        int energyFlipped = 0;
        
        for (int i = x-1; i <= x+1; i+=2) {
            for (int j = y-1; j <= y+1; j+=2) {
              //if (!IsingLattice.isNeighbour(i,j,x,y)) continue;
                int i1 = (i+m)%m;
                int j1 = (j+m)%m;
                energy += -lattice[x][y]*lattice[i1][j1];
                energyFlipped += lattice[x][y]*lattice[i1][j1];
            }
        }
        return (energyFlipped - energy);
    }
   
 // Calculate magnetization for a given spin configuration  
    public double magnetization(){
        double total = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                total+=lattice[i][j];
            }
            }
        
        return total;
    }
   
    public void glauber(int size, double temperature) {
               /*********************GLAUBER SIMULATION******************/
                 Random randomFlip = new Random();
                 
              // Run it for *size* iterations
		 for (int i = 0; i< size;i++ )
                 {  
                   
		// Flip a spin at a random position 
		   int x = randomFlip.nextInt(m);
		   int y = randomFlip.nextInt(m);
                // Find energy difference that we get from flip   
		   int energyDifference = this.calculateEnergyDiff(x,y); 
		   
                //   Create a copy of our lattice object
                //     IsingLattice testLattice = new IsingLattice(lattice,m);
                   
                // Generate random number between 0-1 and implement Metropolis-Glauber
                   
                // int initialEnergy = this.calculateEnergy();
                   
                   if (energyDifference < 0) {
                        this.doFlip(x,y);
                       
                       //  System.out.printf("Total Energy difference by flipping at position (%d,%d) is: %d\n\n",x,y,energyDifference);
                       // int finalEnergy = this.calculateEnergy();
                       // System.out.printf("%d \n",finalEnergy-initialEnergy);
                    }
                       else {
                            double r = randomFlip.nextDouble();
                            if (r < IsingModel.probability(temperature, energyDifference))  {
                               this.doFlip(x,y);
                             
                             //  System.out.printf("Total Energy difference by flipping at position (%d,%d) is: %d\n\n",x,y,energyDifference);
                             //  int finalEnergy = this.calculateEnergy();
                             //  System.out.printf("here %d ---- %f \n",finalEnergy-initialEnergy,IsingModel.probability(temperature, energyDifference));
                            }
                        }
                            //    else{            
                            //      if (this.equals(testLattice))
                            //      System.out.printf("We did not update because energy difference at position: (%d,%d) is: %d\n\n",x,y,energyDifference);
                            //     }             
                            //lattice.printLattice();
                            // else stay in the same state.    
                            // else System.out.println(i);    
                 }
               //  System.out.printf("Average magnetisation at temperature %f is: %f\n",temperature,mag/totalTransitions);
        }
    
    public void kawasaki (int iterations, double temperature){
            /*********************KAWASAKI SIMULATION******************/
                 int energyDifference = 0;
                 Random randomSeed = new Random();
		 for (int i = 0; i< iterations;i++ )
                 {  
                   
		// Flip two spins at a random position if they are different 
		   int x1 = randomSeed.nextInt(m);
		   int y1 = randomSeed.nextInt(m);
                   int x2 = randomSeed.nextInt(m);
                   int y2 = randomSeed.nextInt(m);
                   
                   // I think you add 2 if you have neighbours since you've got 2X the same interaction
                   // if you don't have neighbours you just add the two energy differences
                   if (this.lattice[x1][y1] !=  this.lattice[x2][y2]){
                       
                       if (IsingLattice.isNeighbour(x1,y1,x2,y2) == true)
                        energyDifference = this.calculateEnergyDiff(x1,y1)+this.calculateEnergyDiff(x2,y2) +4;
                       else energyDifference = this.calculateEnergyDiff(x1,y1)+this.calculateEnergyDiff(x2,y2);
                   
                   
                   
                     if (energyDifference <= 0) {
                        this.doFlip(x1,y1); 
                        this.doFlip(x2,y2);
                       // System.out.printf("Kawasaki: Total Energy difference by flipping positions (%d,%d) with (%d,%d) is: %d\n\n",x1,y1,x2,y2,energyDifference);
                      }
                       else {
                            // Generate random number between 0-1 and implement Metropolis-Kawasaki
                               double r = randomSeed.nextDouble();
                                if (r < IsingModel.probability(temperature, energyDifference))  {
                                    this.doFlip(x1,y1);
                                    this.doFlip(x2,y2);
                            //   System.out.printf("Kawasaki: Total Energy difference by flipping positions (%d,%d) with (%d,%d) is: %d\n\n",x1,y1,x2,y2,energyDifference);
                                } 
                            }
                   }
                       
                 }
        }
            
        
    public int calculateEnergy() {
        int totalEnergy = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m - 1; j++) {
                if (i != 0) {
                    totalEnergy = totalEnergy - lattice[i][j] * lattice[i - 1][j];
                }

                totalEnergy = totalEnergy - lattice[i][j] * lattice[i][j + 1];
            }
            if (i != 0) {
                totalEnergy = totalEnergy - lattice[i][m - 1] * lattice[i - 1][m - 1];
            }
        }

        return totalEnergy;

    }
}

            
        
   
    
    