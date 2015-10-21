package org.phenoscape.owl.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Temporary ComparisonScore implementation for testing purposes. This object 
 * will essentially hold all important information from an individual row of the 
 * scores_gene_taxon.tsv file
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
