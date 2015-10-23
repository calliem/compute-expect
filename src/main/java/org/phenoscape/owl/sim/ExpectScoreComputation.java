package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.Map;

public interface ExpectScoreComputation<ID> {

	/**
	 * 
	 * @param scores. contents of the scores_genes_taxon file. collection of comparisonscore objects
	 * @param corpusProfileSizes
	 * @param queryProfileSizes
	 * @return
	 */
	public Map<ID, Double> computeExpectScores(Collection<ComparisonScore<ID>> scores, Map<ID, Integer> corpusProfileSizes, Map<ID, Integer> queryProfileSizes);
	
}
