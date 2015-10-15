import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Regression {

    public static void main (String[] args) {
        int numTaxa = 659; // hardcoded temporarily

        Map<String, Integer> profSizes = loadProfileSizes("/results/ProfileSizes.txt");
        System.out.println(profSizes);

    }

    /*
     * public int[] getScores(String inputFile){
     * Path resultsDir = inputFile;
     * 
     * 
     * scores=[]
     * geneprofilesizes=[]
     * taxonprofilesizes=[]
     * rawscores=[]
     * 
     * loadProfileSizes("../results/ProfileSizes.txt");
     * return null;
     * }
     */

    public static Map<String, Integer> loadProfileSizes (String inputSizesFile) {
        Map<String, Integer> profileSize = new HashMap<String, Integer>();

        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(inputSizesFile));
            String line = input.readLine();
            while (line != null)
            {
                System.out.println(line);
                line = input.readLine();

                String[] splitString = line.trim().split("\t");
                String entity = splitString[0];
                entity = entity.replace("#profile", "");
                entity = entity.replace("http://purl.obolibrary.org/obo/", "");
                String size = splitString[1];
                profileSize.put(entity, Integer.parseInt(size));
            }
            
            input.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return profileSize;
    }
}
