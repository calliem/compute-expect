package org.phenoscape.owl.sim;

import java.util.Collection;
import java.util.Map;

public interface ExpectScoreComputation<ID> {

	public Map<ID, Double> computeExpectScores(Collection<ComparisonScore<ID>> scores, Map<ID, Integer> corpusProfileSizes, Map<ID, Integer> queryProfileSizes);

}
