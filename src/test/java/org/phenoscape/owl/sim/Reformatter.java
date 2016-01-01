package org.phenoscape.owl.sim;

import java.util.Scanner;


/**
 * Reformats data printed out from the ExpectScoreComputer and outputs a vector that can be copied directly into R
 * @author Callie Mao
 *
 */
public class Reformatter {
	
	public static final void main(String[] args){
	    Scanner scanner = new Scanner( System.in );
	    System.out.print( "Enter X output (from Java)" );
	    
	    String curInput = scanner.nextLine();
	    String input = "";
	    while (!curInput.equals("ff")){
	    	input += curInput;
	    	curInput = scanner.nextLine();
	    }
	    
	    String[] splitInput = input.split("\t");
	    String x1 = ""; 
	    String x2 = "";
	    
	    String x1Java = "";
	    for (int i = 0; i < splitInput.length; i ++){
	    	if (i % 2 == 0){ //x1
	    		x1 += splitInput[i];
	    		x1Java += "{" + splitInput[i] + "," + splitInput[i+1] + "}";
				if (i != splitInput.length - 1){
					x1 += ",";
					x1Java+= ",\n";
				}
	    	}
	    	else{
	    		x2 += splitInput[i];
	    		if (i != splitInput.length - 1)
	    			x2 += ",";
	    	}

	    }
	    System.out.println(x1);
	    System.out.println();
	    System.out.println(x2);
	    System.out.println();
	    System.out.println(x1Java);
	}
	
}
