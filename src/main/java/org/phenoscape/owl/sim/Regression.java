package org.phenoscape.owl.sim;

/**
 * Temporary translation of expect computation script.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Regression {

	// TODO: public static final String inputsDir
	// TODO: organize paths
	public static final String absPath = new File("").getAbsolutePath();
	public static final String resultsDir = "/data/results/";

	// input files
	public static final String profileSizesPath = resultsDir
			+ "ProfileSizes.txt";
	public static final String scoresGeneTaxonPath = resultsDir
			+ "Scores_Gene_Taxon.tsv";

	// output files
	public static final String scoreSizesPath = resultsDir + "Scores_Sizes.txt";

	public static void main(String[] args) {
		int numTaxa = 659; // hardcoded temporarily
		// getScores(); //TODO: uncomment when all completed. this code has
		// already been run

		// Load taxon, gene profile sizes and similarity scores

		List<List<Double>> returnList = loadProfiles(scoreSizesPath);
		// questionable design. consider just making these global. change
		// accordingly with eventual interface
		List<Double> taxonProfileSizes = returnList.get(0);
		List<Double> geneProfileSizes = returnList.get(1);
		List<Double> scores = returnList.get(2);
	}

	public static void getScores() {
		// TODO: os package code
		// if not os.path.exists(resultdir):
		// os.makedirs(resultdir)
		Map<String, Integer> profSizes = loadProfileSizes(profileSizesPath);
		queryParseResults(profSizes, scoreSizesPath, scoresGeneTaxonPath);
	}

	public static void queryParseResults(Map<String, Integer> size,
			String outputScoresFile, String inputScoresFile) {

		String absPath = new File("").getAbsolutePath();

		PrintWriter scoreFile;
		try {
			scoreFile = new PrintWriter(absPath + outputScoresFile, "UTF-8");
			scoreFile
					.write("Gene\tGene Profile Size\tGene Name\tTaxon\tTaxon Profile Size\tTaxon Name\tScore\tURI\n");

			BufferedReader inFile;
			try {
				inFile = new BufferedReader(new FileReader(absPath
						+ inputScoresFile));

				String line = inFile.readLine();
				while (line != null) {
					if (!line.contains("gene_label")) {
						String[] splitLine = line
								.trim()
								.replace("\"", "")
								.replace(
										"^^<http://www.w3.org/2001/XMLSchema#string>",
										"")
								.replace(
										"^^<http://www.w3.org/2001/XMLSchema#double>",
										"").replace("<", "").replace(">", "")
								.replace("http://purl.obolibrary.org/obo/", "")
								.split("\t");
						String uri = splitLine[0];
						String score = splitLine[1];
						String gene = splitLine[2];
						String geneName = splitLine[3];
						String taxon = splitLine[4];
						String taxonName = splitLine[5];

						scoreFile.write(gene + "\t"
								+ String.valueOf(size.get(gene)) + "\t"
								+ geneName + "\t" + taxon + "\t"
								+ String.valueOf(size.get(taxon)) + "\t"
								+ taxonName + "\t" + score + "\t" + uri + "\n");
					}
					line = inFile.readLine();
				}
				inFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			scoreFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static List<List<Double>> loadProfiles(String inFile) {
		List<Double> scores = new ArrayList<Double>();
		List<Double> geneProfileSizes = new ArrayList<Double>();
		List<Double> taxonProfileSizes = new ArrayList<Double>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(absPath
					+ inFile));
			String line = input.readLine();

			while (line != null) {
				if (!line.contains("Score") && !line.contains("score")) { // TODO:
																			// ignore
																			// case
					String[] data = line.trim().split("\t");
					double score = Double.parseDouble(data[6]);
					scores.add(score);
					geneProfileSizes.add(Math.log(Integer.parseInt(data[1])));
					taxonProfileSizes.add(Math.log(Integer.parseInt(data[4])));
				}
				line = input.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<List<Double>> returnList = new ArrayList<List<Double>>();
		returnList.add(scores);
		returnList.add(geneProfileSizes);
		returnList.add(taxonProfileSizes);
		return returnList; // profileSize;
	}

}
