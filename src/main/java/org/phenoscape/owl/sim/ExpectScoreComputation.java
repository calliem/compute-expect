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
	//make an implementation of comparisonScore
	
		//stick to using the interface because he is going to send something different
		
		// 659 is the number of things in corpusprofilesizes
		
		// inspect ID = uri of the profile. he'll just give 2 collections
		
		//ID can just be a string. 
		//corpus profile sizes would require checking the ID in the score sizes file
		//if VTO id it is a taxon. if not then it is a gene. 
		
		//integer is the size
		
		//corpusprofile size = tax profiles. how big the phenotypic profile. how many phenotypic profiles are associated with it in a taxon
		// queryprofilesize = gene profiles (how many phenotypes are associated with each gene)
		// both are phenotypic profiles. both unioned together will becoem profilesize from teh script
		
		//he will call and he will give comparisonscore
		//don't worry about comparisonscore since jim will implement it
		//ID is generic 
		//sometimes he uses java uri and sometimes he uses the owl uri
		//when i send the id back in the result, then he'll have the real type. 
		//no methods to call on ID
}
