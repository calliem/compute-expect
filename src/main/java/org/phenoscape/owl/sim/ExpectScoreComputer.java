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
	
	private double[] y;
	private double[][] x;
	private Map<ID, Integer> identifiers;
	
	@Override
	public Map<ID, Double> computeExpectScores(
			Collection<ComparisonScore<ID>> comparisons,
			Map<ID, Integer> corpusProfileSizes,
			Map<ID, Integer> queryProfileSizes) {
		    int numTaxa = corpusProfileSizes.size(); 
			formatData(comparisons, corpusProfileSizes, queryProfileSizes);
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			double[] coefficients = regM(regression);
			return calculateExpectScoresMap(regression, coefficients, numTaxa);
	}

	/**
	 * Takes inputted comparison, corpusProfileSizes, and queryProfileSizes collections and reformats them into 
	 * global arrays for both the response variable (1-D array) and the independent variables (array with 3 columns).
	 * The array for the response variable contains an additional column of one's to represent the intercept. 
	 * Response and independent variables both occupy the same rows index within their respective array, and this index
	 * is stored within the identifiers variable (for later lookup). 
	 * @param comparisons
	 * @param corpusProfileSizes
	 * @param queryProfileSizes
	 */
	private void formatData(Collection<ComparisonScore<ID>> comparisons, Map<ID, Integer> corpusProfileSizes, Map<ID, Integer> queryProfileSizes){
		y = new double[comparisons.size()];
		x = new double[comparisons.size()][numColumns];
		identifiers = new HashMap<ID, Integer>();
		int i = 0;
		for (ComparisonScore<ID> s: comparisons){
			identifiers.put(s.id(), i);
			y[i] = s.similarity();
			x[i][0] = 1;
			x[i][1] = Math.log(queryProfileSizes.get(s.queryProfile()));
			x[i][2] = Math.log(corpusProfileSizes.get(s.corpusProfile()));
			i++;
		}
	}
	
	private double[] regM (OLSMultipleLinearRegression regression){
		regression.setNoIntercept(true);
		regression.newSampleData(y, x);
        double[] parameterEstimates = regression.estimateRegressionParameters();
		return parameterEstimates;
	}
	
	private Map<ID, Double> calculateExpectScoresMap(OLSMultipleLinearRegression regression, double[] coefficients, int numTaxa){
		double[] residuals = regression.estimateResiduals();
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
		// calculates just the diagonal portion of the matrix
		RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
		RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		double rss = regression.calculateResidualSumOfSquares();
		double sigma = (rss) / (y.length-3);
		
		for (ID URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			double hii = calculateHii(xMatrix, xMatrixSquaredInverse, index);
			double studentizedResidual = studentize(sigma, residuals[index], hii);
			double expectScore = computeExpect(studentizedResidual, numTaxa);
			expectScores.put(URI, expectScore);
		}
		return expectScores;
	}

	private double calculateHii(RealMatrix xMatrix, RealMatrix xMatrixSquaredInverse, int index){ 
		RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
		RealMatrix hiiAsMatrix = firstHalf.multiply((xMatrix.getRowMatrix(index)).transpose());
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
