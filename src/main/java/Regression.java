import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class Regression {

    public static void main (String[] args) {
        int numTaxa = 659; // hardcoded temporarily

        Map<String, Integer> profSizes = loadProfileSizes("/data/results/ProfileSizes.txt");
        System.out.println("profsizes " + profSizes);
        System.out.println("profsizes " + profSizes.get("http://www.informatics.jax.org/marker/MGI:95921"));


    }

  

    public static Map<String, Integer> loadProfileSizes (String inputSizesFile) {
        Map<String, Integer> profileSize = new HashMap<String, Integer>();

        BufferedReader input;
        try {
        	String absPath = new File("").getAbsolutePath();
        	input = new BufferedReader(new FileReader(absPath + inputSizesFile));
            
            String line = input.readLine();
            while (line != null)
            {
                String[] splitString = line.trim().split("\t");
                String entity = splitString[0];
                entity = entity.replace("#profile", "");
                entity = entity.replace("http://purl.obolibrary.org/obo/", "");
                String size = splitString[1];
                profileSize.put(entity, Integer.parseInt(size));
                line = input.readLine();
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
