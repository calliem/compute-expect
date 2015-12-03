package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ExpectScoreComputer<ID> implements ExpectScoreComputation<ID> { 
	//template the String to a generic object
	
	private static final int numColumns = 3;
	private OLSMultipleLinearRegression regression;
	private double[] y;
	private double[][] x;
	private Map<ID, Integer> identifiers;
	private static final int numTaxa = 659;
	
	//TODO: probably make this one global
//	private Collection<ComparisonScore<String>> scores;
	
	@Override
	public Map<ID, Double> computeExpectScores(
			Collection<ComparisonScore<ID>> comparisons,
			Map<ID, Integer> corpusProfileSizes,
			Map<ID, Integer> queryProfileSizes) {
		// TODO Auto-generated method stub
		
		//Map<String, Double> studentizedResiduals = computeStudentizedResiduals(coefficients); //TODO: use uib.basecode.math.
		// int numTaxa = queryProfileSizes.size(); // TODO: database size (should this be passed in somewhere or parsed?)

			//this.scores = scores; //TODO: if use this as a global, remove this from the parameters that are passed around
			formatData(comparisons, corpusProfileSizes, queryProfileSizes); //TODO: pass back a RegressionData object and pass that into parameters below
			double[] coefficients = regM();
			return calculateExpectScoresMap(coefficients);
	
	}
	
	//to keep the same identifiers, we include a map of the URI to the i index, or can consider just using a map for URI's to Y and another map for URI's to x.
	// ^ Actually above will probably not work because API takes in arrays and that's extra work to convert the array
	/**
	 * Y columns and X columns must match identifiers (i's). 
	 * @param comparisons
	 * @param corpusProfileSizes
	 * @param queryProfileSizes
	 */
	private void formatData(Collection<ComparisonScore<ID>> comparisons, Map<ID, Integer> corpusProfileSizes, Map<ID, Integer> queryProfileSizes){
		y = new double[comparisons.size()];
		x = new double[comparisons.size()][numColumns];
		System.out.println("x is a matrix of size " + comparisons.size() + "x" + (numColumns));
		identifiers = new HashMap<ID, Integer>();
		int i = 0;
		for (ComparisonScore<ID> s: comparisons){
			identifiers.put(s.id(), i);
			// vectorize response variable
			y[i] = s.similarity();
			
			// setup dependent variables
			x[i][0] = 1;
			x[i][1] = Math.log(queryProfileSizes.get(s.queryProfile())); //TODO: remove magic values
			x[i][2] = Math.log(corpusProfileSizes.get(s.corpusProfile()));
			
			i++;
			
			/*-0.15852367904452747
			0.05047672528746634
			0.052837912126559035*/
		}
		
//		System.out.println();
//		System.out.println("DATA");
//		System.out.println("-----------");
//		System.out.println("Y");
//		System.out.println("Y.length = " + y.length); // truncated from the shorter scores_genes_taxon file
//		for (Double yi: y){
//			System.out.print(yi + ",");
//		}
//		
//		System.out.println();
//		System.out.println("-----------");
//		System.out.println("X: genes \t\t taxons \t\t constant");
		//printDoubleArray(x);
		
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
		regression.setNoIntercept(true);
		regression.newSampleData(y, x);
		
		// regression.
		
		
		
		
		
		System.out.println(regression.isNoIntercept());
		System.out.println("calculating hat");
		regression.calculateHat();
		System.out.println("done calculating hat");
        double[] parameterEstimates = regression.estimateRegressionParameters();
        System.out.println("done estimating regression parameters");
		return parameterEstimates;
	}
	
//	private void printDoubleArray(double[][] test){
//		for (int i = 0; i<test.length; i++){
//		    for (int j = 0; j<test[i].length; j++){
//		        System.out.print(test[i][j] + "\t");
//		    }
//		    System.out.println();
//		}
//	}
	
	private Map<ID, Double> calculateExpectScoresMap(double[] coefficients){
		System.out.println("Calculating studentized residuals");
		RealMatrix hatMatrix = regression.calculateHat();
		double[] residuals = regression.estimateResiduals();
		
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
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
		//System.out.println();
		
		RealMatrix xMatrix = new Array2DRowRealMatrix(x);
	//	System.out.println("xMatrix");
	//	System.out.println(xMatrix);
		RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
	//	System.out.println("xMatrixSquared");
//		System.out.println(xMatrixSquared);
		RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
	//	System.out.println("xMatrixSquaredInverse");
	//	System.out.println(xMatrixSquaredInverse);
		
		for (ID URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			double[] xVector = x[index];
			double yValue = y[index];
			
	//		System.out.println();
		//	System.out.println("URI: " + URI);
			//double sigma = (regression.calculateResidualSumOfSquares() - Math.pow(residuals[index],2)) / (y.length-3-1); // substract residuals
			// calculating internal studentized residuals 
			double sigma = (regression.calculateResidualSumOfSquares()) / (y.length-3);
//			System.out.println("sigma " + sigma);
			
			// calculate hii
			//System.out.println("Matrix printing");
			RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
			RealMatrix hiiAsMatrix = firstHalf.multiply(xMatrix.transpose());
		//	System.out.println(hiiAsMatrix);
			double hii = hiiAsMatrix.getEntry(0, 0);
			//System.out.println(index + " : " + hii);
			
			double studentizedResidual = studentize(sigma, residuals[index], hii); //hatMatrix.getEntry(index, index));
			double expectScore = computeExpect(studentizedResidual, numTaxa);
	//		System.out.println("expectScore: " + expectScore);
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
//		System.out.println("Studentized Residual: " + rawResidual/(Math.sqrt(sigma * (1-hii))));
//		System.out.println("raw residual: " + rawResidual); // matches R
//		System.out.println("hii: " + hii);
//
//		System.out.println("more testing:");
//		System.out.println("denominator: " +  Math.sqrt((sigma) * (1-hii)));
		return rawResidual/(Math.sqrt(sigma * (1-hii)));
		// result is calculated correctly, but lack of precision in R suggests a different answer. Lack of precision in python matches the unprecise R code.
		// Using studres() in R gives a different result entirely
	}



	
	/* For studentized residuals comparison purposes:
	 	URI: http://purl.org/phenoscape/uuid/70dd3fa4-3cde-40f2-8ed6-799f92b17077
		Java: 0.5591214336265494
		Python: 0.571616694057
		R studres package: 0.55995704
		R by hand: 0.5591214
		
		For expect score:
		Java: 364.1178879169344
		Python: 378.916326656
	*/
	
	/* URI: http://purl.org/phenoscape/uuid/b5b8de28-aaa2-466a-9809-dd8754f52565
	   Python: 348.311894559
	   Java: 331.42292875911903
	  */
	
	/* URI: */
	
	
}
