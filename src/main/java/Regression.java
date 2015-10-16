import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Regression {

	public static void main(String[] args) {
		int numTaxa = 659; // hardcoded temporarily

		getScores();

	}

	public static void getScores() {
		String resultsDir = "/data/results/";

		// TODO: os package code
		Map<String, Integer> profSizes = loadProfileSizes(resultsDir + "ProfileSizes.txt");
		//System.out.println("profsizes " + profSizes);
//		System.out
//				.println("profsizes "
//						+ profSizes
//								.get("http://www.informatics.jax.org/marker/MGI:95921"));

		//Map<String, Integer> size = loadProfileSizes("/results/ProfileSizes.txt");
		queryParseResults(profSizes, resultsDir + "Scores_Sizes.txt",
				resultsDir + "Scores_Gene_Taxon.tsv");
	}

	public static void queryParseResults(Map<String, Integer> size,
		String outputScoresFile, String inputScoresFile) {
		// Map<String, Integer> topScores = new HashMap<String, Integer>();
		// Map<String, Integer> maxScore = new HashMap<String, Integer>();
		// Map<String, Integer> maxTaxon = new HashMap<String, Integer>();
		// Map<String, Integer> geneSet = new HashMap<String, Integer>();
		// Map<String, Integer> name = new HashMap<String, Integer>();
		// Map<String, Integer> taxonId = new HashMap<String, Integer>();

		String absPath = new File("").getAbsolutePath();

		PrintWriter scoreFile;
		try {
			scoreFile = new PrintWriter(absPath + outputScoresFile, "UTF-8");//new FileWriter(absPath
					//+ outputScoresFile)); //TODO: make this create a new file
			scoreFile.write("Gene\tGene Profile Size\tGene Name\tTaxon\tTaxon Profile Size\tTaxon Name\tScore\tURI\n");

			BufferedReader inFile;
			try {
				inFile = new BufferedReader(new FileReader(absPath
						+ inputScoresFile));

				String line = inFile.readLine();
				while (line != null) {
					if (!line.contains("gene_label")) {
						System.out.println("---" + line);
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
						
						for (int i = 0; i < splitLine.length; i ++){
							System.out.print(splitLine[i] + " ");
						}
						System.out.println();

//						scoreFile.write(gene + "\t"
//								+ String.valueOf(size.get(gene)) + "\t"
//								+ geneName + "\t" + taxon + "\t"
//								+ String.valueOf(size.get(taxon)) + "\t"
//								+ taxonName + "\t" + score + "\t" + uri + "\n");
					}
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
		
		System.out.println(profileSize.keySet());
		System.out.println();
		System.out.println(profileSize.values());
		return profileSize;
	}

}
