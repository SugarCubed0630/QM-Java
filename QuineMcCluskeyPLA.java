import java.io.*;
import java.util.*;

public class QuineMcCluskeyPLA 
{
    private static int numInputs;  //number of input
    private static int numOutputs;  //number of output
    private static ArrayList<Integer> minTerms = new ArrayList<>();  //stores list of minterms
    private static ArrayList<Integer> dontCares = new ArrayList<>();  //stores list of ignored terms

    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);

        //prompts 
        System.out.print("Enter input PLA file path: ");
        String inputFilePath = scanner.nextLine();

        System.out.print("Enter output PLA file path: ");
        String outputFilePath = scanner.nextLine();

        try {
            //parse in PLA file
            parsePLA(inputFilePath);

            //process the terms, converting to binary representation
            ArrayList<String> binaryTerms = new ArrayList<>();
            for(int term : minTerms)
            {
                binaryTerms.add(convertToBinary(term, numInputs));
            }
            for(int term : dontCares)
            {
                binaryTerms.add(convertToBinary(term, numInputs));
            }

            ArrayList<ArrayList<String>> groups = groupByOnes(binaryTerms);  //group and minimize terms
            ArrayList<String> primeImplicants = minimize(groups);  //implement Quine-McCluskey method

            writePLA(outputFilePath, primeImplicants);  //write a minimized result into PLA format

            System.out.println("Minimized PLA written to " + outputFilePath);
        }catch(IOException e) {
            System.err.println("Error processing PLA file: " + e.getMessage());
        }

        scanner.close();
    }

    //parse in PLA file
    private static void parsePLA(String filePath) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while((line = reader.readLine()) != null)
        {
            line = line.trim();

            if(line.startsWith(".i"))  //number of inputs
            {
                numInputs = Integer.parseInt(line.split("\\s+")[1]);
            } 
            else if(line.startsWith(".o"))  //number of outputs
            {
                numOutputs = Integer.parseInt(line.split("\\s+")[1]);
            } 
            else if (line.startsWith(".p"))  //number of terms 
            {
                int declaredTerms = Integer.parseInt(line.split("\\s+")[1]);
                //compare with the actual count of terms
                if(declaredTerms != actualTerms) 
                {
                    throw new IllegalArgumentException("Mismatch between declared and actual number of terms.");
                }
            }
            else if(line.startsWith(".e"))  //end of PLA file
            {
                break;
            } 
            else if(!line.startsWith("."))  //process product terms, ignore lines starting with '.'
            {
                String[] parts = line.split("\\s+");
                String inputPart = parts[0];
                String outputPart = parts[1];

                int inputDecimal = Integer.parseInt(inputPart.replace("-", "0"), 2);  //convert input to decimal
                if(outputPart.contains("1"))  //add to minterms list if containing '1'
                {
                    minTerms.add(inputDecimal);
                } 
                else  //add to ignore list
                {
                    dontCares.add(inputDecimal);
                }
            }
        }

        reader.close();
    }

    //convert number to binary with leading zeros
    private static String convertToBinary(int number, int length) 
    {
        String binary = Integer.toBinaryString(number);
        while(binary.length() < length)  //padding for length
        {
            binary = "0" + binary;
        }
        return binary;
    }

    //group the binary terms by the number of 1's
    private static ArrayList<ArrayList<String>> groupByOnes(ArrayList<String> terms) 
  {
        ArrayList<ArrayList<String>> groups = new ArrayList<>();  //list of groups for each possible count of 1's
        for(int i = 0; i <= numInputs; i++) 
        {
            groups.add(new ArrayList<>());
        }
    
        for(String term : terms)  //assigning terms to groups
        {
            int onesCount = countOnes(term);
            groups.get(onesCount).add(term);
        }

        return groups;
    }

    //count the number of 1's in a binary string
    private static int countOnes(String binary) 
    {
        int count = 0;
        for(char c : binary.toCharArray()) 
        {
            if (c == '1') count++;
        }
        return count;
    }

    //Quine-McCluskey method
    private static ArrayList<String> minimize(ArrayList<ArrayList<String>> groups) 
    {
        ArrayList<String> primeImplicants = new ArrayList<>();

        ArrayList<ArrayList<String>> nextGroups = new ArrayList<>();  //list for next group of terms
        for(int i = 0; i < groups.size() - 1; i++) 
        {
            ArrayList<String> group = groups.get(i);
            ArrayList<String> nextGroup = groups.get(i + 1);
            ArrayList<String> combinedGroup = new ArrayList<>();

            for(String term1 : group)  //compares terms in consecutive groups, combining when possible
            {
                boolean combined = false;

                for(String term2 : nextGroup) 
                {
                    String combinedTerm = combineTerms(term1, term2);
                    if(combinedTerm != null) 
                    {
                        combinedGroup.add(combinedTerm);
                        combined = true;
                    }
                }

                if(!combined)  //prime implicant is found when a term can not be combined
                {
                    primeImplicants.add(term1);
                }
            }

            nextGroups.add(combinedGroup);
        }

        for(ArrayList<String> group : nextGroups)  //add remaining terms as prime implicants
        {
            primeImplicants.addAll(group);
        }

        return primeImplicants;
    }

    //combine the two binary terms if they differ by one bit
    private static String combineTerms(String term1, String term2) 
    {
        StringBuilder result = new StringBuilder();
        int diffCount = 0;

        for(int i = 0; i < term1.length(); i++) 
        {
            if(term1.charAt(i) == term2.charAt(i)) 
            {
                result.append(term1.charAt(i));
            } 
            else 
            {
                result.append('-');
                diffCount++;
            }

            if(diffCount > 1) 
            {
              return null;
            }
        }

        return result.toString();
    }

    //write a minimized result into PLA file
    private static void writePLA(String filePath, ArrayList<String> implicants) throws IOException 
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        writer.write(".i " + numInputs + "\n");  //number of inputs
        writer.write(".o " + numOutputs + "\n");  //number of outputs

        for(String implicant : implicants)  //outputs each implicant with the corresponding output value
        {
            writer.write(implicant.replace('-', 'X') + " 1\n");
        }

        writer.write(".e\n");  //end of PLA file
        writer.close();
    }
}
