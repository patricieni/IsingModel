/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isingmodel;
import java.awt.Dimension;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
/**
 *
 * @author patric
 */

public class IsingModel {

    public static void main(String[] args) throws IOException {
	
            
               
		System.out.println("Hello and Welcome back again");		
		int m =0;
                double temperature = 0;
                String dynamics = "";
                int vis = 0;
                String iterate = "";
                
             // Read the temperature, size and dynamics of the system
                Scanner in = new Scanner(System.in);
                System.out.println("Please introduce ");
                temperature =  IsingModel.introduce(temperature,in,"temperature");
                m =  IsingModel.introduce(m,in,"dimension");
                dynamics = IsingModel.introduce(dynamics,in,"dynamics");
                vis = IsingModel.introduce(vis,in,"visualization"); 
                System.out.println("Do you want to iterate over temperatures?");
                iterate = IsingModel.introduce(iterate,in);
                
             // Print to file temperature-magnetization pair with errors, same for heat Capacity
                PrintWriter writer = new PrintWriter("magnetization.dat","UTF-8");
                PrintWriter writer1 = new PrintWriter("susceptibility.dat","UTF-8");
                PrintWriter writer2 = new PrintWriter("energy.dat","UTF-8");
                PrintWriter writer3 = new PrintWriter("heat.dat","UTF-8");
                
             // Build a random configuration of spin up-down at a fixed temperature
                int iterations = m*m;
                IsingLattice lattice = new IsingLattice(m); 
		IsingModelViewer modelViewer = new IsingModelViewer(lattice);
               
                
                if (vis!=0) IsingModel.runVisualization(modelViewer);
             
               
             // Start with Glauber Visualization
                if (dynamics.equals("glauber") || dynamics.equals("g")) {
                // Problem with glauber at least that it converges to fast and it's because i used a heuristic for getting 
                    // the value of the equilibrium time. 
                     if (iterate.equals("yes") || iterate.equals("y")) IsingModel.runGlauberDynamics(iterations, temperature, vis,m,writer,writer1,modelViewer,lattice);
                        else
                            IsingModel.runGlauber(iterations, temperature, vis, m, modelViewer, lattice);
                }
                // KAWASAKI VISUALIZATION HERE
		else {
                    if (iterate.equals("yes") || iterate.equals("y")) IsingModel.runKawasakiDynamics(iterations,temperature,vis,m, writer2,writer3,modelViewer,lattice); 
                        else 
                            IsingModel.runKawasaki(iterations, temperature, vis, m, modelViewer, lattice);
                }
                
                   writer1.close(); 
		   writer.close();
                   writer2.close();
                   writer3.close();
	
         }
    
          public static void runGlauberDynamics(int iterations, double temperature, int vis, int m,
                                               PrintWriter writer,PrintWriter writer1,
                                               IsingModelViewer modelViewer, IsingLattice lattice){     
                    
               double mag,magSquared,averageMag,susceptibility,averageMagError;
               int totalCounts;
               //ArrayList<Double> susceptibilityList;
               //susceptibilityList = new ArrayList<Double>();
               lattice.initializeLattice();
               //double temperature1 = temperature;
               System.out.println(temperature);
               while (temperature <4){
                mag = 0;
                magSquared = 0;
                averageMag = 0;
                susceptibility = 0;
                totalCounts = 0;
                averageMagError = 0;
               
              //  lattice.initializeLattice();
               // if (temperature != temperature1) lattice = new IsingLattice(m);
                
          /*     
             // Find lowest energy transition, hence equilibrium state. For small temperatures the system doesn't have enough energy to move out of the state =>
             // random magnetization that you don't expect otherwise.
                boolean equilibrated = false;
                double energyAux=0;
                double energyAuxOld = -9999;
                while (!equilibrated){
                    for (int j =0; j<100;j++){
                        
                        energyAux+=lattice.calculateEnergy();
                        
                        lattice.glauber(iterations,temperature);
                       // if (vis!=0) modelViewer.repaint();
                    }    
                    energyAux = energyAux/100.0;
                    //if (Math.abs(temperature -0.4)<0.0001)
                 //           System.out.println(energyAux);
                    if (Math.abs(energyAux-energyAuxOld) < 0.01) equilibrated = true;
                    else energyAuxOld = energyAux;
                }
            //    */ 
                
             // Run it for a lot of MC cycles instead of infinite loop; get magnetisation each 10 or 100 cycles after equilibrium!!!
		for(int k =0;k<iterations*50;k++) {
                        
			lattice.glauber(iterations,temperature);
                       // System.out.println("Value of k is "+k);
                         if (((k%100)==0) && (k>iterations*30)){
                       // if (((k%100) == 0)) {
                         // Get magnetization every 100th or 10th step, magnetization squared respectively
                            mag+=lattice.magnetization();
                            magSquared += lattice.magnetization()*lattice.magnetization();
                            totalCounts+=1;
                        }
                     // Request repaint
			if (vis!=0) modelViewer.repaint();
                     
                        if (totalCounts == 200) break;
                }
                        averageMag = mag/(double)totalCounts; 
                        averageMagError = Math.sqrt(IsingModel.susceptibility(mag, magSquared,totalCounts, temperature)*temperature/(double)(totalCounts-1));
                        susceptibility = IsingModel.susceptibility(mag, magSquared,totalCounts, temperature);
                        
                        //susceptibilityList.add(susceptibility);
                        
                        writer.println(temperature + "\t"+ averageMag+"\t"+averageMagError);
                        writer1.println(temperature+ "\t"+ susceptibility);
                        System.out.printf("Average magnetisation at temperature %f is: %f\n",temperature,averageMag);
                        //System.out.printf("The susceptibility at temperature %f is: %f \n", temperature,susceptibility);
                        temperature+=0.05;
                
             } 
             //  for (double item: susceptibilityList)
                 //    System.out.println("Error on susceptibility: " + IsingModel.jackKnife(item,susceptibilityList));
        }
    

         public static void runKawasakiDynamics(int iterations,double temperature,int vis,int m,
                 PrintWriter writer2,PrintWriter writer3,IsingModelViewer modelViewer, IsingLattice lattice){
                
             double energy,energySquared,averageEnergy,specificHeat,averageEnergyError;
             int totalCounts;
           //  double temperature1 = temperature;
             while (temperature<=4){
                 energy = 0;
                 energySquared = 0;
                 averageEnergy = 0;
                 specificHeat = 0;
                 totalCounts =0;
                 averageEnergyError =0;
             
         /*
             // Find lowest energy transition, hence equilibrium state. For small temperatures the system doesn't have enough energy to move out of the state =>
             // random magnetization that you don't expect otherwise.
                boolean equilibrated = false;
                double energyAux=0;
                double energyAuxOld = -9999;
                while (!equilibrated){
                    for (int j =0; j<100;j++){
                        
                        energyAux+=lattice.calculateEnergy();
                        
                        lattice.glauber(iterations,temperature);
                    }    
                    energyAux = energyAux/100.0;
                    //if (Math.abs(temperature -0.4)<0.0001)
                 //           System.out.println(energyAux);
                    if (Math.abs(energyAux-energyAuxOld) < 0.01) equilibrated = true;
                    else energyAuxOld = energyAux;
                }
          */
                
             // Run it for a lot of MC cycles instead of infinite loop; get magnetisation each 10 or 100 cycles after equilibrium!!!
                for(int k = 0;k<iterations*50;k++) {
                        
			lattice.kawasaki(iterations,temperature);
                        
                        if ((k > iterations*30) &&( (k%100) == 0)) {
                         // Get magnetization every 100th or 10th step
                            energy+=lattice.calculateEnergy();
                            energySquared +=lattice.calculateEnergy()*lattice.calculateEnergy();
                            totalCounts+=1;
                        }
		     // Request repaint
			if (vis !=0)modelViewer.repaint();
                     // IF you have 100 values for magnetization after you've reached equilibrium then get out
                        if (totalCounts == 200) break;
                        
                 }
                     // Calculate average energy and specific heat
                        averageEnergy = energy/(double)totalCounts; 
                        averageEnergyError = Math.sqrt(IsingModel.specificHeat(energy, energySquared, totalCounts, temperature)*temperature / (double)(totalCounts-1));
                        specificHeat =  IsingModel.specificHeat(energy, energySquared, totalCounts, temperature);
                        System.out.printf("Average energy at temperature %f is: %f\n",temperature,averageEnergy);
                     
                     // System.out.printf("The heat capacity at temperature %f is: %f \n", temperature,heatCapacity);
                        writer2.println(temperature+"\t"+ averageEnergy+"\t"+averageEnergyError);
                        writer3.println(temperature+"\t"+specificHeat);
                    
                        temperature+=0.05;
             }
                 
        }
         
         
         public static void runGlauber(int iterations, double temperature, int vis, 
                            int m, IsingModelViewer modelViewer, IsingLattice lattice){
         
            for (;;){            
			lattice.glauber(iterations,temperature);
                     // Request repaint
			if (vis!=0) modelViewer.repaint();
            }
                 
         
         }
         
         public static void runKawasaki(int iterations, double temperature, int vis, 
                            int m, IsingModelViewer modelViewer, IsingLattice lattice){
         
         // for(int k =0;k<iterations*100;k++) {
            for (;;){            
			lattice.kawasaki(iterations,temperature);
                     // Request repaint
			if (vis!=0) modelViewer.repaint();
            }
                 
         
         }
    
         public static double jackKnife(double item, List<Double> cap){
         
             double heatError = 0;
             double [] total = new double[cap.size()];
             for (int i =0;i<cap.size();i++){
                for(int j = 0;j<cap.size();j++)
                     if (j!=i) total[i]+=cap.get(j);
             
                 total[i]=total[i]/(double)(cap.size() - 1)-item;        
             }
             
               for (int i =0;i<cap.size();i++) 
                   heatError += Math.pow(total[i],2);
           return Math.sqrt(heatError);  
             
             
         }
         
         
         
        public static double susceptibility(double mag, double magSquared, int size, double temperature) {
            double squareMagAverage;
            squareMagAverage = magSquared / (double)size;
            return (squareMagAverage - Math.pow(mag / (double)size, 2)) / temperature;
        }
        
        
        public static double specificHeat(double energy, double energySquared, int size, double temperature) {
            double squareEnergyAverage;
            squareEnergyAverage = energySquared / (double)size;
            return (squareEnergyAverage - Math.pow(energy / (double)size, 2)) / temperature;
        }
        

        public static void runVisualization(IsingModelViewer view){
        
            
                
                // Set up a Frame (window) to open on the screen
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Ising Model");
		frame.setPreferredSize(new Dimension(600,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		// Embed the pendulum viewer within the content pane
		frame.getContentPane().add(view);
		
		// Lay out the frame's contents, and put it on screen
		frame.pack();
		frame.setVisible(true);
                
        
        }    
         
        public static int introduce(int m, Scanner in, String message) {
        System.out.print(message + ": ");
        boolean inputError = false;
        while (!inputError) {
            if (in.hasNextInt()) {
                m = in.nextInt();
            } else {
                in.next();
                System.out.println("Invalid" + message + "! Please re-enter " + message + ": ");
                continue;
            }
            inputError = true;
        }
        return m;
    }
        public static double introduce(double temp, Scanner in, String message) {
        System.out.print(message + ": ");
        boolean inputError = false;
        while (!inputError) {
            if (in.hasNextDouble()) {
                temp = in.nextDouble();
            } else {
                in.next();
                System.out.println("Invalid" + message + "! Please re-enter " + message + ": ");
                continue;
            }
            inputError = true;
        }
        return temp;
    }
        public static String introduce(String dynamics, Scanner in, String message) {
        System.out.print(message + ": ");
        boolean inputError = false;
        while (!inputError) {
            if (in.hasNext("k") || in.hasNext("g") || in.hasNext("glauber") || in.hasNext("kawasaki")) {
                dynamics = in.next();
            } else {
                System.out.println("Invalid" + message + "! Please re-enter " + message + ": ");
                in.next();
                continue;
            }
            inputError = true;
        }
        return dynamics;
    }
        public static String introduce(String dynamics, Scanner in) {
        boolean inputError = false;
        while (!inputError) {
            if (in.hasNext("yes") || in.hasNext("no") || in.hasNext("y") || in.hasNext("n")) {
                dynamics = in.next();
            } else {
                System.out.println("Please type 'y', 'n' or 'yes' 'no'");
                in.next();
                continue;
            }
            inputError = true;
        }
        return dynamics;
    }
        
         
        public static double probability(double temperature, int energyDif) {
        return Math.exp(-(double) energyDif / temperature);
        }
	   
 // add more methods here if needed         
 // Remember not to change the lattice.getLattice() as it will change the whole object and you can't
                   // do anything about that apparently 
		// Calculate the initial energy of the system
	/**	   initialEnergy = lattice.calculateEnergy();
		   System.out.printf("Total Energy for the above spin configuration: %d\n",initialEnergy);
		// Flip a spin and calculate the new energy in a different way   
		   lattice.doFlip(x,y);
		   lattice.printLattice();
		   finalEnergy = lattice.calculateEnergy();
		   System.out.printf("Total Energy for the above spin configuration: %d\n",finalEnergy);
          **/         
	          
                  
}
