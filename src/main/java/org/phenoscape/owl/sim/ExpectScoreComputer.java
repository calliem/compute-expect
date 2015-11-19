package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ExpectScoreComputer<String> implements ExpectScoreComputation<String> { 
	//template the String to a generic object
	
	private static final int numColumns = 2;
	private OLSMultipleLinearRegression regression;
	private double[] y;
	private double[][] x;
	private Map<String, Integer> identifiers;
	
	//TODO: probably make this one global
//	private Collection<ComparisonScore<String>> scores;
	
	@Override
	public Map<String, Double> computeExpectScores(
			Collection<ComparisonScore<String>> scores,
			Map<String, Integer> corpusProfileSizes,
			Map<String, Integer> queryProfileSizes) {
		
		//this.scores = scores; //TODO: if use this as a global, remove this from the parameters that are passed around
		formatData(scores, corpusProfileSizes, queryProfileSizes); //TODO: pass back a RegressionData object and pass that into parameters below
		double[] coefficients = regM();
		

		
		//TODO: calculate studentied residuals
		
		Map<String, Double> studentizedResiduals = computeStudentizedResiduals(coefficients); //TODO: use uib.basecode.math.
		// test for same result
		// pass in coefficients and coefficient estimates
		// download jar file
		//http://chibi.ubc.ca/faculty/pavlidis/basecode/dependencies.html
		//http://chibi.ubc.ca/faculty/pavlidis/basecode/apidocs/
		
		 //TODO: how to parse number of taxa
		
		int numTaxa = queryProfileSizes.size(); //TODO: check what numTaxa represents
		
		
		return null; //computeExpect(studentizedResiduals, numTaxa);
	}
	
	//to keep the same identifiers, we include a map of the URI to the i index, or can consider just using a map for URI's to Y and another map for URI's to x.
	// ^ Actually above will probably not work because API takes in arrays and that's extra work to convert the array
	/**
	 * Y columns and X columns must match identifiers (i's). 
	 * @param scores
	 * @param taxonProfileSizes
	 * @param geneProfileSizes
	 */
	private void formatData(Collection<ComparisonScore<String>> scores, Map<String, Integer> taxonProfileSizes, Map<String, Integer> geneProfileSizes){
		y = new double[scores.size()];
		x = new double[scores.size()][numColumns];
		System.out.println("x is a matrix of size " + scores.size() + "x" + (numColumns));
		identifiers = new HashMap<String, Integer>();
		int i = 0;
		for (ComparisonScore<String> s: scores){
			identifiers.put(s.id(), i);
			// vectorize response variable
			y[i] = s.similarity();
			
			// setup dependent variables
			x[i][0] = Math.log(geneProfileSizes.get(s.queryProfile())); //TODO: remove magic values
			x[i][1] = Math.log(taxonProfileSizes.get(s.corpusProfile()));
			i++;
		}
		
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
		printDoubleArray(x);
		
	}
	
	private Map<String, Double> computeExpect(Map<String, Double> studentizedResiduals, int numTaxa) {
		Map<String, Double> expectScore = new HashMap<String, Double>();
		
		for (String ID : studentizedResiduals.keySet()){
			double studRes = studentizedResiduals.get(ID);
			double pValue = 1 - Math.exp(-1 * Math.exp(-1 * studRes * Math.PI / Math.sqrt(6) + 0.5772156649));
			double expect = pValue * numTaxa;
			expectScore.put(ID, expect);
		}
				
		return expectScore; 
	};
	
	private double[] regM (){
		System.out.println();
		System.out.println("Doing Regression");
		
		
		
		regression = new OLSMultipleLinearRegression();		
		regression.newSampleData(y, x);
		// regression.
		
		
		System.out.println(regression.isNoIntercept());
		
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
	
	private Map<String, Double> computeStudentizedResiduals(double[] coefficients){
		System.out.println("Calculating studentized residuals");
		RealMatrix hatMatrix = regression.calculateHat();
		double[] residuals = regression.estimateResiduals();
		
		Map<String, Double> expectScores = new HashMap<String, Double>();
		
		//order of coefficients?
		double constant = coefficients[0];
		double geneCoeff = coefficients[1];
		double taxonCoeff = coefficients[2];
		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		
		double sigma = regression.calculateResidualSumOfSquares() / y.length;
		System.out.println("sigma " + sigma);
		
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		
		for (String URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			double[] xVector = x[index];
			double yValue = y[index];
			System.out.println(URI);
			
			expectScores.put(URI, studentize(sigma, residuals[index], hatMatrix.getEntry(index, index))); //studentized residuals for now
		}
		
		return expectScores;
		
		
		
		//predicted = 
		//rawResidual = 
		//hatMatrix.getEntry(i, j);
		
		
	}

	private double studentize(double sigma, double rawResidual, double hii) {
		//double predictedY = coefficients[0];
//		for (int i = 1; i < coefficients.length; i ++){
//			//always one more coefficient than xvector because of the beta0 intercept
//			predictedY += xVector[i-1] * coefficients[i];
//		}
//		double rawResidual = yValue - predictedY;
//		System.out.println("residual from API " + residual);
		System.out.println(rawResidual/((sigma) * Math.sqrt(1-hii)));
		return rawResidual/((sigma) * Math.sqrt(1-hii));
	}
	
	// get 659 from the test file .... should be passed it correctly 
}
