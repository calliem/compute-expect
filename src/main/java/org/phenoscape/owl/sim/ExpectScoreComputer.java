package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ExpectScoreComputer<ID> implements ExpectScoreComputation<ID> { 
	
	private static final int numColumns = 3;
	private OLSMultipleLinearRegression regression;
	private double[] y;
	private double[][] x;
	private Map<ID, Integer> identifiers;
	//private static final int numTaxa = 659;
	
	//TODO: probably make this one global
//	private Collection<ComparisonScore<String>> scores;
	
	@Override
	public Map<ID, Double> computeExpectScores(
			Collection<ComparisonScore<ID>> comparisons,
			Map<ID, Integer> corpusProfileSizes,
			Map<ID, Integer> queryProfileSizes) {

		    int numTaxa = corpusProfileSizes.size(); 
		    System.out.println("numTaxa " + numTaxa); //check this before moving on
		
			//this.scores = scores; //TODO: if use this as a global, remove this from the parameters that are passed around
			formatData(comparisons, corpusProfileSizes, queryProfileSizes); //TODO: pass back a RegressionData object and pass that into parameters below
			double[] coefficients = regM();
			return calculateExpectScoresMap(coefficients, numTaxa);
	
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
	
	private double[] regM (){
		System.out.println();
		System.out.println("Doing Regression");
		
		regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		regression.newSampleData(y, x);
		
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
	
	private Map<ID, Double> calculateExpectScoresMap(double[] coefficients, int numTaxa){
		System.out.println("Calculating studentized residuals");
		//RealMatrix hatMatrix = regression.calculateHat();
		double[] residuals = regression.estimateResiduals();
		
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
		//order of coefficients?
//		double constant = coefficients[0];
//		double geneCoeff = coefficients[1];
//		double taxonCoeff = coefficients[2];
//		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		//System.out.println();
		
		RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
		RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		for (ID URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			double sigma = (regression.calculateResidualSumOfSquares()) / (y.length-3);
			double hii = calculateHii(xMatrix, xMatrixSquaredInverse, index);

			double studentizedResidual = studentize(sigma, residuals[index], hii);
			double expectScore = computeExpect(studentizedResidual, numTaxa);
			expectScores.put(URI, expectScore);
		}
		return expectScores;
	}
	
	private double calculateHii(RealMatrix xMatrix, RealMatrix xMatrixSquaredInverse, int index){
		RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
		RealMatrix hiiAsMatrix = firstHalf.multiply(xMatrix.transpose());
		return hiiAsMatrix.getEntry(0, 0);
	}
	
	private double computeExpect(double studRes, int databaseSize){
		double pValue = 1 - Math.exp(-1 * Math.exp(-1 * studRes * Math.PI / Math.sqrt(6) + 0.5772156649));
		return pValue * databaseSize;
	}

	private double studentize(double sigma, double rawResidual, double hii) {
		return rawResidual/(Math.sqrt(sigma * (1-hii)));
	}	
}
