package org.phenoscape.owl.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	private static final String absPath = new File("").getAbsolutePath();
	private static final String resultsDir = "/data/results/";
	private static final String scoresGeneTaxonPath = absPath + resultsDir
			+ "Scores_Sizes_rand20.txt";
			//+ "Scores_Gene_Taxon_first5.txt";
//	private static final String profileSizesPath = absPath + resultsDir
//			+ "ProfileSizes.txt";
	
//	private static String queryProfile = "queryProfile";
//	private static String corpusProfile = "queryProfile";
	
	private static Collection<ComparisonScore<String>> scoreList = new ArrayList<ComparisonScore<String>>();
	private static Map<String, Integer> queryProfileSizes = new HashMap<String, Integer>();
	private static Map<String, Integer> corpusProfileSizes = new HashMap<String, Integer>();
	
	public static void main(String[] args) {
		
		// For purposes of simplifying testing code, we instead build ScoreGeneTaxon objects 
		// from the Scores_Sizes file created from running the python script instead of rebuilding it
		// from scratch, although this can be done later on.
		// Collection<ComparisonScore<String>> scoreList = generateScoreGeneTaxons(scoresGeneTaxonPath);
		generateScoreGeneTaxons(scoresGeneTaxonPath);
	//	Map<String, Map<String, Integer>> profileSizes = generateProfileSizes(profileSizesPath);
	//	Map<String, Integer> queryProfileSizes = profileSizes.get(queryProfile);
	//	Map<String, Integer> corpusProfileSizes = profileSizes.get(corpusProfile);
		
		ExpectScoreComputer computeExpect = new ExpectScoreComputer();
		//TODO: how to keep this implementation generic? Probably doesn't fully matter since this is only a testing class that will not be run
		Map<String, Double> expectScores = computeExpect.computeExpectScores(scoreList, corpusProfileSizes, queryProfileSizes);
		//TODO: could technically pull all these parameters from scores_sizes.txt
		//TODO static
	}
	
	/**
	 * Constructor that generates ScoreGeneTaxon objects from the inputted file and returns a collection of them
	 * @param filePath Absolute path to the to-be-parsed file. File should be formatted like example file scores_gene_taxon.tsv
	 */
	public static void generateScoreGeneTaxons(String filePath){
		//Collection<ComparisonScore<String>> scores = new ArrayList<ComparisonScore<String>>();
		try {
			BufferedReader inputFile = new BufferedReader(new FileReader(filePath));
			
			String line = inputFile.readLine();
			int i = 0;
			System.out.println("Reading " + filePath);
			while (line != null) {
//				System.out.print(i + " "); i++;

// For use with ScoreGeneTaxon file:
//				if (!line.contains("gene_label") && !line.contains("taxonname")) {
//					String[] splitLine = line.trim().replace("\"", "")
//							.replace("^^<http://www.w3.org/2001/XMLSchema#string>","")
//							.replace("^^<http://www.w3.org/2001/XMLSchema#double>","").replace("<", "")
//							.replace(">", "").replace("http://purl.obolibrary.org/obo/", "").split("\t");
//					String URI = splitLine[0].replace("#profile", ""); // added in for VTO  				
//					double similarityScore = Double.parseDouble(splitLine[1]); 
//					String gene = splitLine[2]; 
//					String taxon = splitLine[4]; 
//					System.out.println("gene " + gene);
//					System.out.println("taxon " + taxon);
//					scores.add(new ScoreGeneTaxon(URI, similarityScore, gene, taxon)); //TODO: not sure if this will work since implementation doesn't have a constructor
//				}
				
				// For use with score sizes file:
				if (!line.contains("Score") && !line.contains("score")) { // TODO: ignore case
					String[] data = line.trim().split("\t");
					
					double similarityScore = Double.parseDouble(data[6]);
					String URI = data[7];
					String gene = data[0];
					String taxon = data[3];
					scoreList.add(new ScoreGeneTaxon(URI, similarityScore, gene, taxon));		
					queryProfileSizes.put(gene, Integer.parseInt(data[1])); //TODO: sizes need to be logged in the regression
					corpusProfileSizes.put(taxon, Integer.parseInt(data[4]));
				}

				line = inputFile.readLine();
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	//	return scores;
	}
	
//	public static Map<String, Map<String, Integer>> generateProfileSizes(String inputFile) {
//		Map<String, Map<String, Integer>> profileSizes = new HashMap<String, Map<String,Integer>>();
//		Map<String, Integer> queryProfileSizes = new HashMap<String, Integer>();
//		Map<String, Integer> corpusProfileSizes = new HashMap<String, Integer>();
//
//		try {
//			BufferedReader input = new BufferedReader(new FileReader(inputFile));
//
//			String line = input.readLine();
//			final int taxonLimitNum = 8;
//			final int geneLimitNum = 8; 
//			
//			while (line != null) {
//				String[] splitString = line.trim().split("\t");
//				for (String sp: splitString)
//					System.out.println("split " + sp);
//				String URI = splitString[0];
//				URI = URI.replace("#profile", "").replace("http://purl.obolibrary.org/obo/", ""); //TODO: make this into a method since this is used multiple times for VTO
//				//TODO: what about replacing all the extraneous text in the zfin.org ... files
//				int size = Integer.parseInt(splitString[1]);
//				if (URI.contains("VTO_") && corpusProfileSizes.size() < taxonLimitNum) //taxon //TODO: remove && limits in final version. && currently exists for testing
//					corpusProfileSizes.put(URI, size);
//				else if (!URI.contains("VTO_") && queryProfileSizes.size() < geneLimitNum)
//					queryProfileSizes.put(URI, size);
//				else if (queryProfileSizes.size() == geneLimitNum && corpusProfileSizes.size() == taxonLimitNum)
//					break; // TODO: for efficiency during testing. remove this eventually as well
//				line = input.readLine();
//			}
//			input.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		profileSizes.put(queryProfile,queryProfileSizes);
//		profileSizes.put(corpusProfile, corpusProfileSizes);
//		System.out.println("test sizes");
//		System.out.println(queryProfileSizes.size());
//		System.out.println(corpusProfileSizes.size());
//		return profileSizes;
//	}

}
