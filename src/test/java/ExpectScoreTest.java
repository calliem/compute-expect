import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class ExpectScoreTest {
	private static final int digitAccuracy = 10;

	@Test
	public void testExpectScore() {
		final String scoresSizesPathTest = Main.absPath + Main.dataDir + Main.testDir
				+ "/Scores_Sizes_rand20.txt";
		final String pythonResultsRand20 = Main.absPath + Main.dataDir + Main.testDir + "/SemanticSimilarityResults.tsv";
		System.out.println(scoresSizesPathTest);
		Map<String, Double> observedResults = Main.run(scoresSizesPathTest);
		
		// Check that all expect scores match (based upon results printed to text files)
		BufferedReader pythonResults;
		try {
			pythonResults = new BufferedReader(new FileReader(
					pythonResultsRand20));
			String line = pythonResults.readLine();
			while (line != null) {
				if (line.contains("Similarity"))
					continue;
				String[] strArr = line.split(" ");
				String URI = strArr[0];
				double pythonExpectValue = Double.parseDouble(strArr[5]);
				double javaExpectValue = observedResults.get(URI);
				assertEquals("Expect Score for " + URI, pythonExpectValue, javaExpectValue, digitAccuracy);
				line = pythonResults.readLine();
			}
			pythonResults.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

