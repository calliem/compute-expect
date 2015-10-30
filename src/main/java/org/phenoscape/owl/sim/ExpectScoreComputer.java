package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ExpectScoreComputer implements ExpectScoreComputation<String> {
	
	private static final int numColumns = 2;
	
	@Override
	public Map<String, Double> computeExpectScores(
			Collection<ComparisonScore<String>> scores,
			Map<String, Integer> corpusProfileSizes,
			Map<String, Integer> queryProfileSizes) {
		
		double[] coefficients = regM(scores, corpusProfileSizes, queryProfileSizes);

		//order of coefficients?
		double constant = coefficients[0];
		double geneCoeff = coefficients[1];
		double taxonCoeff = coefficients[2];
		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		
		//TODO: calculate studentied residuals
		
		studentize();
		return null;
	}
	
	private double[] regM (Collection<ComparisonScore<String>> scores, Map<String, Integer> taxonProfileSizes, Map<String, Integer> geneProfileSizes){
		System.out.println();
		System.out.println("Doing Regression");
		
		double[] y = new double[scores.size()];
		double[][] x = new double[scores.size()][numColumns];
		System.out.println("x is a matrix of size " + scores.size() + "x" + (numColumns));

		int i = 0;
		for (ComparisonScore<String> s: scores){
			// vectorize response variable
			y[i] = s.similarity();
			
			// setup dependent variables
			x[i][0] = Math.log(geneProfileSizes.get(s.queryProfile())); //TODO: remove magic values
			x[i][1] = Math.log(taxonProfileSizes.get(s.corpusProfile()));
		//	x[i][2] = 1;
			i++;
		}
		
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();		
		regression.newSampleData(y, x);
		
		System.out.println();
		System.out.println("DATA");
		System.out.println("-----------");
		System.out.println("Y");
		System.out.println("Y.length = " + y.length); // truncated from the shorter scores_genes_taxon file
		for (Double yi: y){
			System.out.print(yi + ",");
		}
		
		System.out.println();
		System.out.println("-----------");
		System.out.println("X: genes \t\t taxons \t\t constant");
//		System.out.println(xTruncated.length);
		printDoubleArray(x);
		
		System.out.println(regression.isNoIntercept());
//		System.out.println(regression.getX());
		regression.calculateHat();
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
	
	private void studentize(){
		System.out.println("Calculating studentized residuals");
		
		
	}
}
