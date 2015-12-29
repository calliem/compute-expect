package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import no.uib.cipr.matrix.DenseMatrix;

public class ExpectScoreComputer<ID> implements ExpectScoreComputation<ID> { 
	
	private static final int numColumns = 3;
	private OLSMultipleLinearRegression regression;
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
			double[] coefficients = regM();
			return calculateExpectScoresMap(coefficients, numTaxa);
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
		System.out.println("x is a matrix of size " + comparisons.size() + "x" + (numColumns));
		identifiers = new HashMap<ID, Integer>();
		int i = 0;
		for (ComparisonScore<ID> s: comparisons){
			identifiers.put(s.id(), i);
			// vectorize response variable
			y[i] = s.similarity();
			// setup independent variables
			x[i][0] = 1;
			x[i][1] = Math.log(queryProfileSizes.get(s.queryProfile()));
			x[i][2] = Math.log(corpusProfileSizes.get(s.corpusProfile()));
			i++;
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
//		printDoubleArray(x);
		
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
	
	private Map<ID, Double> calculateExpectScoresMap(double[] coefficients, int numTaxa){
		System.out.println("Calculating studentized residuals");
		double[] residuals = regression.estimateResiduals();
		
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		
		// calculates just the diagonal portion of the matrix
		RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
		RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		System.out.println("matrices calculated");
		
		double rss = regression.calculateResidualSumOfSquares();
		double sigma = (rss) / (y.length-3);
		
		int i = 0;
		long startTime = System.currentTimeMillis();;
		for (ID URI: identifiers.keySet()){
			int index = identifiers.get(URI);
			if (i % 100000 == 0){
				long time2 = System.currentTimeMillis();
				System.out.println(i + " " + (time2-startTime));
			}
			//192 ms for 10,000
				
			//System.out.println("RSS " + rss);
			//System.out.println(sigma);;
//			long time2 = System.currentTimeMillis();
//			System.out.println("calulate sigma (residual sum of squares) " + (time2 - startTime));
			
			double hii = calculateHii(xMatrix, xMatrixSquaredInverse, index);
//			long time3 = System.currentTimeMillis();
//			System.out.println("calculate Hii " + (time3 - time2));

//			long time4 = System.currentTimeMillis();
			double studentizedResidual = studentize(sigma, residuals[index], hii);
//			long time5 = System.currentTimeMillis();
//			System.out.println("calculate stud res " + (time5 - time4));
			
//			long time6 = System.currentTimeMillis();
			double expectScore = computeExpect(studentizedResidual, numTaxa);
//			long time7 = System.currentTimeMillis();
//			System.out.println("calculate expect " + (time7 - time6));
			
			expectScores.put(URI, expectScore);
//			System.out.println(URI + " " + hii + " " + x[index][1] + " " + x[index][2]);
			i++;
		}
		return expectScores;
	}
	
	// MTJ: For testing purposes
	// For future testing: consider EMTJ for small matrix math calculations
	private Map<ID, Double> calculateExpectScoresMTJ(double[] coefficients, int numTaxa){
		System.out.println("Calculating studentized residuals");
		double[] residuals = regression.estimateResiduals();
		
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		
		// calculates just the diagonal portion of the matrix
		/*DenseMatrix xMatrix = new DenseMatrix(x);
		//RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		DenseMatrix xMatrixSquared = new DenseMatrix(x.length, x.length);
		xMatrix.transAmult(xMatrix, xMatrixSquared);*/
		//DoubleMatrix2D xMatrixSquaredInverse = linAlg.inverse(xMatrixSquared);
		//xMatrixSquared.inverse();
		
		// No direct inverse matrix calculation in MTJ, so to preserve efficiency Apache Commons Math is used
		// The resulting 3x3 matrix will be converted back into a DenseMatrix
		RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
		RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		double[][] xSquaredInverseArray = xMatrixSquaredInverse.getData();
		DenseMatrix xMat = new DenseMatrix(x);
		DenseMatrix xSquaredInverse = new DenseMatrix(xSquaredInverseArray);
		
		//RealMatrix xMatrixSquared = xMatrix.transpose().multiply(xMatrix);
		//RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		System.out.println("matrices calculated");
		
		int i = 0;
		for (ID URI: identifiers.keySet()){
			
			int index = identifiers.get(URI);
			long startTime = 0;
			if (i < 100)
				startTime = System.currentTimeMillis();
			double sigma = (regression.calculateResidualSumOfSquares()) / (y.length-3);
			long time2 = System.currentTimeMillis();
			System.out.println("calulate sigma (residual sum of squares) " + (time2 - startTime));
			
			double hii = calculateHiiMTJ(xMat, xSquaredInverse, index);
			long time3 = System.currentTimeMillis();
			System.out.println("calculate Hii " + (time3 - time2));

			long time4 = System.currentTimeMillis();
			double studentizedResidual = studentize(sigma, residuals[index], hii);
			long time5 = System.currentTimeMillis();
			System.out.println("calculate stud res " + (time5 - time4));
			
			long time6 = System.currentTimeMillis();
			double expectScore = computeExpect(studentizedResidual, numTaxa);
			long time7 = System.currentTimeMillis();
			System.out.println("calculate expect " + (time7 - time6));
			
			expectScores.put(URI, expectScore);
			System.out.println(URI + " " + hii + " " + x[index][1] + " " + x[index][2]);
			if (i < 100)
				System.out.println("full expect score calculation: " + (System.currentTimeMillis() - startTime));
			i++;
		}
		return expectScores;
	}
	
	
	// Colt: For testing purposes
	private Map<ID, Double> calculateExpectScoresMapColt(double[] coefficients, int numTaxa){
		System.out.println("Calculating studentized residuals");
		double[] residuals = regression.estimateResiduals();
		
		Map<ID, Double> expectScores = new HashMap<ID, Double>();
		
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("-----------");
		System.out.println("coefficients");
		for (int i = 0; i < coefficients.length; i++){
			System.out.println(coefficients[i]);
		}
		
		Algebra linAlg = new Algebra();
		
		// calculates just the diagonal portion of the matrix
		//RealMatrix xMatrix = new Array2DRowRealMatrix(x);
		DoubleMatrix2D xMatrix = new DenseDoubleMatrix2D(x);
		DoubleMatrix2D xMatrixSquared = new DenseDoubleMatrix2D(x.length, x.length);
		(xMatrix.viewDice()).zMult(xMatrix, xMatrixSquared);
		DoubleMatrix2D xMatrixSquaredInverse = linAlg.inverse(xMatrixSquared);
		//RealMatrix xMatrixSquaredInverse = new LUDecomposition(xMatrixSquared).getSolver().getInverse();
		
		System.out.println("matrices calculated");
		
		int i = 0;
		for (ID URI: identifiers.keySet()){
			
			int index = identifiers.get(URI);
			long startTime = 0;
			if (i < 100)
				startTime = System.currentTimeMillis();
			double sigma = (regression.calculateResidualSumOfSquares()) / (y.length-3);
			long time2 = System.currentTimeMillis();
			System.out.println("calulate sigma (residual sum of squares) " + (time2 - startTime));
			
			double hii = calculateHiiColt(xMatrix, xMatrixSquaredInverse, index);
			long time3 = System.currentTimeMillis();
			System.out.println("calculate Hii " + (time3 - time2));

			long time4 = System.currentTimeMillis();
			double studentizedResidual = studentize(sigma, residuals[index], hii);
			long time5 = System.currentTimeMillis();
			System.out.println("calculate stud res " + (time5 - time4));
			
			long time6 = System.currentTimeMillis();
			double expectScore = computeExpect(studentizedResidual, numTaxa);
			long time7 = System.currentTimeMillis();
			System.out.println("calculate expect " + (time7 - time6));
			
			expectScores.put(URI, expectScore);
			System.out.println(URI + " " + hii + " " + x[index][1] + " " + x[index][2]);
			if (i < 100)
				System.out.println("full expect score calculation: " + (System.currentTimeMillis() - startTime));
			i++;
		}
		return expectScores;
	}
	
	private double calculateHiiMTJ(DenseMatrix xMatrix, DenseMatrix xMatrixSquaredInverse, int index){
		//1x3 * 3x3 = 1x3
		DenseMatrix firstHalf = new DenseMatrix(1, x[0].length);
		
		double[][] tempMatrix = new double[1][x[index].length];
		tempMatrix[0] = x[index];
		
		double[][] tempTransposed = new double[x[index].length][1];
		tempTransposed[0][0] = x[index][0];
		tempTransposed[1][0] = x[index][1];
		tempTransposed[2][0] = x[index][2];
		
		DenseMatrix row = new DenseMatrix(tempMatrix); //xMatrix.getRowMatrix(index)
		DenseMatrix rowTransposed = new DenseMatrix(tempTransposed);
		
		row.mult(xMatrixSquaredInverse, firstHalf);
		//RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
		// 1x3 * 3x100000
		DenseMatrix hii = new DenseMatrix(1,1);
		firstHalf.mult(rowTransposed, hii); // firsthalf is 1x3 and rowTransposed is 3x1
		return hii.get(0,0);
	}
	
	private double calculateHiiColt(DoubleMatrix2D xMatrix, DoubleMatrix2D xMatrixSquaredInverse, int index){
		//1x3 * 3x3 = 1x3
		DoubleMatrix1D firstHalf = new DenseDoubleMatrix1D(x[0].length); 
		DoubleMatrix1D row = new DenseDoubleMatrix1D(x[index]); //xMatrix.getRowMatrix(index)
		xMatrixSquaredInverse.zMult(row, firstHalf);
		//RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
		// 1x3 * 3x100000
		double hii = firstHalf.zDotProduct(new DenseDoubleMatrix1D(x[index]));
		return hii;
	}
	
	private double calculateHii(RealMatrix xMatrix, RealMatrix xMatrixSquaredInverse, int index){
		//1x3 * 3x3 
		RealMatrix firstHalf = xMatrix.getRowMatrix(index).multiply(xMatrixSquaredInverse);
		// 1x3 * 3x100000
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
	
//	private void printDoubleArray(double[][] test){
//	for (int i = 0; i<test.length; i++){
//	    for (int j = 0; j<test[i].length; j++){
//	        System.out.print(test[i][j] + "\t");
//	    }
//	    System.out.println();
//	}
//}
}
