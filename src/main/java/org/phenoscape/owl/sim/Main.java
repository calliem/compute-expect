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

public class Main {
	
	public static final String absPath = new File("").getAbsolutePath();
	public static final String resultsDir = "/data/results/";
	public static final String scoresGeneTaxonPath = absPath + resultsDir
			+ "Scores_Gene_Taxon.tsv";
	
	public static void main(String[] args) {
		List<ScoreGeneTaxon> scoreList = generateScoreGeneTaxons(scoresGeneTaxonPath);
		System.out.println(scoreList);
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
	
	public static Map<String, Integer> loadProfileSizes(String inputSizesFile) {
		Map<String, Integer> profileSize = new HashMap<String, Integer>();

		BufferedReader input;
		try {
			String absPath = new File("").getAbsolutePath();
			input = new BufferedReader(new FileReader(absPath + inputSizesFile));

			String line = input.readLine();
			while (line != null) {
				String[] splitString = line.trim().split("\t");
				String entity = splitString[0];
				entity = entity.replace("#profile", "");
				entity = entity.replace("http://purl.obolibrary.org/obo/", "");
				String size = splitString[1];
				profileSize.put(entity, Integer.parseInt(size));
				line = input.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return profileSize;
	}

}
