package org.phenoscape.owl.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main implements ExpectScoreComputation<String>{
	
	private static final String absPath = new File("").getAbsolutePath();
	private static final String resultsDir = "/data/results/";
	private static final String scoresGeneTaxonPath = absPath + resultsDir
			+ "Scores_Gene_Taxon.tsv";
	private static final String profileSizesPath = absPath + resultsDir
			+ "ProfileSizes.txt";
	
	private static String queryProfile = "queryProfile";
	private static String corpusProfile = "queryProfile";
	
	public static void main(String[] args) {
		List<ScoreGeneTaxon> scoreList = generateScoreGeneTaxons(scoresGeneTaxonPath);
		System.out.println(scoreList);
		
		Map<String, Map<String, Integer>> profileSizes = generateProfileSizes(profileSizesPath);
		Map<String, Integer> queryProfileSizes = profileSizes.get(queryProfile);
		Map<String, Integer> corpusProfileSizes = profileSizes.get(corpusProfile);
		
		computeExpectScores(scoreList, queryProfileSizes, corpusProfileSizes);
		//TODO remove static
	}
	
	/**
	 * Constructor that generates ScoreGeneTaxon objects from the inputted file and returns a collection of them
	 * @param filePath Absolute path to the to-be-parsed file. File should be formatted like example file scores_gene_taxon.tsv
	 */
	public static List<ScoreGeneTaxon> generateScoreGeneTaxons(String filePath){
		List<ScoreGeneTaxon> scores = new ArrayList<ScoreGeneTaxon>();
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
					String URI = splitLine[0]; 
					double similarityScore = Double.parseDouble(splitLine[1]); 
					String gene = splitLine[2]; 
					String taxon = splitLine[4]; 
					scores.add(new ScoreGeneTaxon(URI, similarityScore, gene, taxon));
				}
				line = inputFile.readLine();
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scores;
	}
	
	public static Map<String, Map<String, Integer>> generateProfileSizes(String inputFile) {
		Map<String, Map<String, Integer>> profileSizes = new HashMap<String, Map<String,Integer>>();
		Map<String, Integer> queryProfileSizes = new HashMap<String, Integer>();
		Map<String, Integer> corpusProfileSizes = new HashMap<String, Integer>();

		try {
			BufferedReader input = new BufferedReader(new FileReader(inputFile));

			String line = input.readLine();
			while (line != null) {
				String[] splitString = line.trim().split("\t");
				String URI = splitString[0];
				URI = URI.replace("#profile", "").replace("http://purl.obolibrary.org/obo/", "");
				//TODO: what about replacing all the extraneous text in the zfin.org ... files
				int size = Integer.parseInt(splitString[1]);
				if (URI.contains("VTO_")) //taxon
					corpusProfileSizes.put(URI, size);
				else
					queryProfileSizes.put(URI, size);
				line = input.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO: return 2 or create an object
		profileSizes.put(queryProfile,queryProfileSizes);
		profileSizes.put(corpusProfile, corpusProfileSizes);
		return profileSizes;
	}
	
	@Override
	public Map computeExpectScores(Collection scores, Map corpusProfileSizes,
			Map queryProfileSizes) {
		// TODO Auto-generated method stub
		return null;
	}

}
