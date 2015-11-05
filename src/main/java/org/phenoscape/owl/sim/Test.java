package org.phenoscape.owl.sim;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class Test {

	
	public static void main(String[] args) {
		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[6][5];
		x[0] = new double[]{0, 0, 0, 0, 0};
		x[1] = new double[]{2.0, 0, 0, 0, 0};
		x[2] = new double[]{0, 3.0, 0, 0, 0};
		x[3] = new double[]{0, 0, 4.0, 0, 0};
		x[4] = new double[]{0, 0, 0, 5.0, 0};
		x[5] = new double[]{0, 0, 0, 0, 6.0};    
		
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(y, x);
		double[] beta = regression.estimateRegressionParameters();     
		for (Double i : beta)
			System.out.println(i);
	}
}
