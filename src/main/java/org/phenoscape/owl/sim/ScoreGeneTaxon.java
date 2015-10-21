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
	
	/**
	 * Constructor that generates all variables from the inputted file
	 * @param filePath Absolute path to the to-be-parsed file. File should be formatted like example file scores_gene_taxon.tsv
	 */
	public ScoreGeneTaxon(String filePath){
		try {
			BufferedReader inputFile = new BufferedReader(new FileReader(filePath));
			
			String line = inputFile.readLine();
			int i = 0;
			while (line != null) {
				System.out.println(i); i++;
				if (!line.contains("gene_label") && !line.contains("taxonname")) {
					String[] splitLine = line.trim().replace("\"", "")
							.replace("^^<http://www.w3.org/2001/XMLSchema#string>","")
							.replace("^^<http://www.w3.org/2001/XMLSchema#double>","").replace("<", "")
							.replace(">", "").replace("http://purl.obolibrary.org/obo/", "").split("\t");
					myID = splitLine[0]; // URI
					mySimilarity = Double.parseDouble(splitLine[1]); // score
					myQueryProfile = splitLine[2]; // gene
					myCorpusProfile = splitLine[4]; // taxon
				}
				line = inputFile.readLine();
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
