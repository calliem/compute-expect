package org.phenoscape.owl.sim;

/**
 * Temporary ComparisonScore implementation for testing purposes. This object 
 * holds all relevant information from an individual row of the 
 * scores_gene_taxon.tsv file that is necessary for expect score computation
 * @author Callie Mao
 *
 */
public class ScoreGeneTaxon implements ComparisonScore<String>{

	private String myID; 
	private double mySimilarity;
	private String myQueryProfile; 
	private String myCorpusProfile; 
	
	public ScoreGeneTaxon(String id, double similarity, String gene, String taxon) {
		myID = id;
		mySimilarity = similarity;
		myQueryProfile = gene;
		myCorpusProfile = taxon;
	}
	
	@Override
	public String id() {
		return myID;
	}

	@Override
	public double similarity() {
		return mySimilarity;
	}

	/**
	 * Return gene string
	 */
	@Override
	public String queryProfile() {
		return myQueryProfile;
	}
	
	/**
	 * Return taxon string
	 */
	@Override
	public String corpusProfile() {
		return myCorpusProfile;
	}
}
