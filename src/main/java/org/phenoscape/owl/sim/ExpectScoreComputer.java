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
		
		// Assumed that the value of scores being passed in is exactly as parsed from the documents. 
		// They are iterated through and logged. 
		// TODO: double check this

		double[] coefficients = regM(scores, corpusProfileSizes, queryProfileSizes);
		double geneCoeff = coefficients[0];
		double taxonCoeff = coefficients[1];
		double constant = coefficients[2];
		
		System.out.println("-----------");
		System.out.println("coefficients");
		System.out.println(geneCoeff);
		System.out.println(taxonCoeff);
		System.out.println(constant);
		
		//TODO: calculate studentied residuals
		
		return null;
	}
	
	private double[] regM (Collection<ComparisonScore<String>> scores, Map<String, Integer> taxonProfileSizes, Map<String, Integer> geneProfileSizes){
		System.out.println();
		System.out.println("Doing Regression");
		
		double[] y = new double[scores.size()];
		double[][] x = new double[scores.size()][numColumns];
		System.out.println("x is " + scores.size() + " x" + numColumns);

		int i = 0;
		for (ComparisonScore<String> s: scores){
			// vectorize response variable
			y[i] = s.similarity();
			
			// setup dependent variables
			System.out.println(s.id());
			System.out.println(s.queryProfile());
			System.out.println(s.corpusProfile());
			System.out.println("gene " + geneProfileSizes);
			System.out.println("taxon " + taxonProfileSizes);
			System.out.println(s.queryProfile());
			System.out.println(geneProfileSizes.get(s.queryProfile()));
			x[i][0] = Math.log(geneProfileSizes.get(s.queryProfile())); //TODO: remove magic values
			x[i][1] = Math.log(taxonProfileSizes.get(s.corpusProfile()));
			x[i][2] = 1;
			
			i++;
		}
		
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();		
		regression.newSampleData(y, x);
		
		System.out.println("-----------");
		System.out.println("Y");
		System.out.println("Y.length = " + y.length); // truncated from the shorter scores_genes_taxon file
		for (Double yi: y){
			System.out.print(yi + " ");
		}
		
		System.out.println();
		System.out.println("-----------");
		System.out.println("X: genes \t\t taxons \t\t constant");
//		System.out.println(xTruncated.length);
		printDoubleArray(x);
		
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
