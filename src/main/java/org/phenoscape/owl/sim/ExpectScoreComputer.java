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
	private static final int numTaxa = 659;
	
	//TODO: probably make this one global
//	private Collection<ComparisonScore<String>> scores;
	
	@Override
	public Map<String, Double> computeExpectScores(
			Collection<ComparisonScore<String>> scores,
			Map<String, Integer> corpusProfileSizes,
			Map<String, Integer> queryProfileSizes) {
		
		//Map<String, Double> studentizedResiduals = computeStudentizedResiduals(coefficients); //TODO: use uib.basecode.math.
				// int numTaxa = queryProfileSizes.size(); // TODO: database size (should this be passed in somewhere or parsed?)
		
		//this.scores = scores; //TODO: if use this as a global, remove this from the parameters that are passed around
		formatData(scores, corpusProfileSizes, queryProfileSizes); //TODO: pass back a RegressionData object and pass that into parameters below
		double[] coefficients = regM();
		return calculateExpectScoresMap(coefficients);
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
	
//	private Map<String, Double> computeExpect(Map<String, Double> studentizedResiduals, int numTaxa) {
//		Map<String, Double> expectScore = new HashMap<String, Double>();
//		
//		for (String ID : studentizedResiduals.keySet()){
//			double studRes = studentizedResiduals.get(ID);
//			double pValue = 1 - Math.exp(-1 * Math.exp(-1 * studRes * Math.PI / Math.sqrt(6) + 0.5772156649));
//			double expect = pValue * numTaxa;
//			expectScore.put(ID, expect);
//		}
//				
//		return expectScore; 
//	};
	
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
	
	private Map<String, Double> calculateExpectScoresMap(double[] coefficients){
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
		
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		System.out.println();
		
		double sigma = regression.calculateResidualSumOfSquares() / y.length;
		System.out.println("sigma " + sigma);
		
		for (String URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			double[] xVector = x[index];
			double yValue = y[index];
			
			System.out.println();
			System.out.println("URI: " + URI);
			
			double studentizedResidual = studentize(sigma, residuals[index], hatMatrix.getEntry(index, index));
			double expectScore = computeExpect(studentizedResidual, numTaxa);
			System.out.println("expectScore: " + expectScore);
			expectScores.put(URI, expectScore); //studentized residuals for now
		}
		
		return expectScores;
		
		
		
		//predicted = 
		//rawResidual = 
		//hatMatrix.getEntry(i, j);
	}
	
	private double computeExpect(double studRes, int databaseSize){
		double pValue = 1 - Math.exp(-1 * Math.exp(-1 * studRes * Math.PI / Math.sqrt(6) + 0.5772156649));
		return pValue * databaseSize;
	}

	private double studentize(double sigma, double rawResidual, double hii) {
		System.out.println("Studentized Residual: " + rawResidual/(Math.sqrt(sigma * (1-hii))));
		System.out.println("raw residual: " + rawResidual); // matches R
		System.out.println("hii: " + hii);

		System.out.println("more testing:");
		System.out.println("denominator: " +  Math.sqrt((sigma) * (1-hii)));
		return rawResidual/(Math.sqrt(sigma * (1-hii)));
		// result is calculated correctly, but lack of precision in R suggests a different answer. Lack of precision in python matches the unprecise R code.
		// Using studres() in R gives a different result entirely
	}
	
	/* For studentized residuals comparison purposes:
	 	URI: http://purl.org/phenoscape/uuid/70dd3fa4-3cde-40f2-8ed6-799f92b17077
		Java: 0.6200053556932885
		Python: 0.571616694057
		R: 0.55995704
		
		For expect score:
		Java: 364.1178879169344
		Python: 378.916326656
	*/
	
	/* URI: http://purl.org/phenoscape/uuid/b5b8de28-aaa2-466a-9809-dd8754f52565
	   Python: 348.311894559
	   Java: 331.42292875911903
	  */
}
