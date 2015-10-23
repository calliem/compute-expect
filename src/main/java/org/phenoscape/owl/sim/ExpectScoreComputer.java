package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.Map;

public class ExpectScoreComputer implements ExpectScoreComputation<String> {

	@Override
	public Map<String, Double> computeExpectScores(
			Collection<ComparisonScore<String>> scores,
			Map<String, Integer> corpusProfileSizes,
			Map<String, Integer> queryProfileSizes) {
		return null;
	}
	
	private int regM (int scores, Map<String, Integer> taxonProfileSizes, Map<String, Integer> geneProfileSizes){
		System.out.println("Doing Regression");
		
		OLSMultipleLinearRegression lol = new OLSMultipleLinearRegression();
		//TODO: why doesn't maven work? where is the maven refresh button?
		
		.newSampleData(double[] y,
                double[][] x)
        .estimateRegressionParameters()
		
		
		return 0;
	}
}
