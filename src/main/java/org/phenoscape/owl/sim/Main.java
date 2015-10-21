package org.phenoscape.owl.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static final String absPath = new File("").getAbsolutePath();
	public static final String resultsDir = "/data/results/";
	public static final String scoresGeneTaxonPath = absPath + resultsDir
			+ "Scores_Gene_Taxon.tsv";
	
	public static void main(String[] args) {
		ScoreGeneTaxon scores = new ScoreGeneTaxon(scoresGeneTaxonPath);
		System.out.println(scores);
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
