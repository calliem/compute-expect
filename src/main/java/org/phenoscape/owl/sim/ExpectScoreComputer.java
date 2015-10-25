package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ExpectScoreComputer implements ExpectScoreComputation<String> {
	
	private static final int numColumns = 3;
	
	@Override
	public Map<String, Double> computeExpectScores(
			Collection<ComparisonScore<String>> scores,
			Map<String, Integer> corpusProfileSizes,
			Map<String, Integer> queryProfileSizes) {
		
		double[] coefficients = regM(scores, corpusProfileSizes, queryProfileSizes);
		double geneCoeff = coefficients[0];
		double taxonCoeff = coefficients[1];
		double constant = coefficients[2];
		
		System.out.println("coefficients");
		System.out.println(geneCoeff);
		System.out.println(taxonCoeff);
		System.out.println(constant);
		
		//TODO: calculate studentied residuals
		
		return null;
	}
	
	private double[] regM (Collection<ComparisonScore<String>> scores, Map<String, Integer> taxonProfileSizes, Map<String, Integer> geneProfileSizes){
		System.out.println("Doing Regression");
		
		if (taxonProfileSizes.size() != geneProfileSizes.size()){
			System.err.println("(Temporary) error: gene and taxon x_i vectors should be of the same size");
		}
		//TODO: what is this was a map? how would this method work?
		
		// vectorize response variable
		double[] y = new double[scores.size()];
		int i = 0;
		for (ComparisonScore<String> s: scores){
			y[i] = s.similarity();
			i++;
		}
		
		// setup dependent variables
		double[][] x = new double[geneProfileSizes.size()][numColumns];
		
		int col = 0;
		int row = 0;
		for (Integer genes: geneProfileSizes.values()){
			x[row][col] = genes;
			row++;
		}
		
		col = 1;
		row = 0;
		for (Integer taxons: taxonProfileSizes.values()){
			x[row][col] = taxons;
			row ++;
		}
		
		col = 2;
		for (row = 0; row < x.length; row++){
			x[row][col] = 1;
		}
		
		System.out.println("Arrays");
	//	for (int k = 0; k < y.length; k++)
	//		System.out.println(y[k]);
		System.out.println("y length: " + y.length); // length: 199
//		System.out.println();
//		System.out.println("print x");
//		printDoubleArray(x);
//		System.out.println(x.length);
//		System.out.println(x[1].length);
		
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();		
		
		//Truncate x for testing purposes
		double[][] xTruncated = new double[y.length][3];
		for (int k = 0; k < y.length; k++){
			for (int j = 0; j < 3; j++)
			xTruncated[k][j] = x[k][j];
		}
		//System.out.println(xTruncated.length);
		//System.out.println(xTruncated[1].length);
		regression.newSampleData(y, xTruncated);
		
		System.out.println("xtruncated");
		System.out.println(xTruncated.length);
		printDoubleArray(xTruncated);
		
        double[] parameterEstimates = regression.estimateRegressionParameters();
		return parameterEstimates;
	}
	
	private void printDoubleArray(double[][] test){
		for (int i = 0; i<test.length; i++){
		    for (int j = 0; j<test[i].length; j++){
		        System.out.print(test[i][j] + "\t");
		    }
		    System.out.println();
		}
	}
}
