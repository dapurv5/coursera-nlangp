import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Solution to part-2 of the assignment.
 */
public class CKYDecoder {
  
  private final Map3K<String, String, String, Integer> cntBinaryRules;
  private final Map2K<String, String, Integer>         cntUnaryRules;
  private final Map<String, Integer>                   cntNonterminals;
  
  private final Map3K<String, String, String, Double> qBinary;
  private final Map2K<String, String, Double> qUnary;
  
  private final BufferedReader reader;
  
  public CKYDecoder(String trainingFile){
    cntBinaryRules  = new Map3K<>();
    cntUnaryRules   = new Map2K<>();
    cntNonterminals = new HashMap<>();
    
    qBinary = new Map3K<>();
    qUnary  = new Map2K<>();
    
    try{
      reader = new BufferedReader(new FileReader(trainingFile));
      
    } catch(FileNotFoundException e){
      System.err.println("Could not find file");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  private void readCounts() throws IOException{
    String line;
    while((line = reader.readLine()) != null){
      String[] tokens = line.split(" ");
      switch(tokens[1]){
        case "NONTERMINAL":
          cntNonterminals.put(tokens[2], Integer.parseInt(tokens[0]));
          break;
        case "UNARYRULE":
          cntUnaryRules.put(tokens[2], tokens[3], Integer.parseInt(tokens[0]));
          break;
        case "BINARYRULE":
          cntBinaryRules.put(tokens[2], tokens[3], tokens[4], Integer.parseInt(tokens[0]));
          break;
      }
    }
  }
  
  private void computeBinaryProbabilities(){
    
  }
  
  private void computeUnaryProbabilities(){
    
  }
  
  public void train() throws IOException{
    readCounts();
  }
  

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_train.counts.out",
    };
    
    CKYDecoder cky = new CKYDecoder(args[0]);
    cky.train();
  }
}