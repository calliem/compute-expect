package org.phenoscape.owl.sim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Main {

	private static final String absPath = new File("").getAbsolutePath();
	private static final String resultsDir = "/data/results/";
	private static final String scoresSizesPath = absPath + resultsDir
			//+ "Scores_Sizes.txt";
			+ "Scores_Sizes_rand20.txt";
	private static final String profileSizesPath = absPath + resultsDir
			+ "ProfileSizes.txt";

	private static Collection<ComparisonScore<String>> scoreList = new ArrayList<ComparisonScore<String>>();
	private static Map<String, Integer> queryProfileSizes = new HashMap<String, Integer>();
	private static Map<String, Integer> corpusProfileSizes = new HashMap<String, Integer>();

	public static void main(String[] args) {
		/*
		 * For testing purposes, we build ScoreGeneTaxon objects from the 
		 * scores_sizes.txt file created from running the python script. 
		 * We do this instead of rebuilding it. 
		 */
		generateSGTfromScoresSizes(scoresSizesPath);

		ExpectScoreComputer<String> computeExpect = new ExpectScoreComputer<String>();
		Map<String, Double> expectScores = computeExpect.computeExpectScores(
				scoreList, corpusProfileSizes, queryProfileSizes);
		
		/*
		 * For testing purposes, write these to a text file for comparison with 
		 * python script results
		 */
		printResultsToTxt(expectScores);
	}
	
	public static void printResultsToTxt(Map<String, Double> scores)
	{
		String resultsFilePath = "java_results.txt";
		System.out.println("Writing to text file");
		FileWriter fstream;
	    BufferedWriter out;

	    try {
			fstream = new FileWriter(resultsFilePath);
			out = new BufferedWriter(fstream);
			for (String URI: scores.keySet()){
				out.write(URI + "\t,\t" + scores.get(URI) + "\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   System.out.println("Results done writing to " + resultsFilePath);
	} 
	
	/**
	 * Constructor that generates ScoreGeneTaxon objects from the inputted file
	 * and returns a collection of them
	 * 
	 * @param filePath
	 *            Absolute path to the to-be-parsed file. File should be
	 *            formatted like example file scores_gene_taxon.tsv
	 */
	public static void generateSGTfromScoresSizes(String filePath) {
		try {
			BufferedReader inputFile = new BufferedReader(new FileReader(
					filePath));

			String line = inputFile.readLine();
			int i = 0;
			System.out.println("Reading " + filePath);
			while (line != null) {
				if (i % 100000 == 0) // total of 10703479 lines
					System.out.println("Reading line " + i);

				if (!line.contains("Score") && !line.contains("score")) { 
					// TODO:ignore case
					String[] data = line.trim().split("\t");

					double similarityScore = Double.parseDouble(data[6]);
					String URI = data[7];
					String gene = data[0];
					String taxon = data[3];
					scoreList.add(new ScoreGeneTaxon(URI, similarityScore,
							gene, taxon));
					queryProfileSizes.put(gene, Integer.parseInt(data[1]));
					corpusProfileSizes.put(taxon, Integer.parseInt(data[4]));
				}
				line = inputFile.readLine();
				i++;
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished reading " + filePath);
		System.out.println();
	}
}
