import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
  private final Map2K<String, String, Double>         qUnary;

  private final String   trainFile; 
  private BufferedReader trainingReader;
  private BufferedReader testReader;
  private PrintWriter    resultsWriter;

  public CKYDecoder(String trainingFile, String testFile, String outputFile){
    cntBinaryRules  = new Map3K<>();
    cntUnaryRules   = new Map2K<>();
    cntNonterminals = new HashMap<>();
    trainFile       = trainingFile;
    setupIO(trainingFile, testFile, outputFile);

    qBinary = new Map3K<>();
    qUnary  = new Map2K<>();
  }

  private void setupIO(String trainFile, String testFile, String outputFile){
    try{
      trainingReader = new BufferedReader(new FileReader(trainFile));
      testReader     = new BufferedReader(new FileReader(testFile));
      resultsWriter  = new PrintWriter(new File(outputFile));

    } catch(FileNotFoundException e){
      System.err.println("Could not find file");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private void readCounts() throws IOException{
    String line;
    while((line = trainingReader.readLine()) != null){
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
    trainingReader.close();
  }

  private void computeProbabilities() throws IOException{
    trainingReader = new BufferedReader(new FileReader(trainFile)); //reopen training file.
    String line;
    while((line = trainingReader.readLine()) != null){
      String[] tokens = line.split(" ");
      double d = 0.0d;
      switch(tokens[1]){
        case "NONTERMINAL":
          continue;
        case "UNARYRULE":
          d = cntUnaryRules.get(tokens[2], tokens[3])/cntNonterminals.get(tokens[2]);
          qUnary.put(tokens[2], tokens[3], d);
          break;
        case "BINARYRULE":
          d = cntBinaryRules.get(tokens[2], tokens[3], tokens[4])/cntNonterminals.get(tokens[2]);
          qBinary.put(tokens[2], tokens[3], tokens[4], d);
          break;
      }
    }
    trainingReader.close();
  }

  private double q(String X, String Y1, String Y2){
    return (qBinary.get(X, Y1, Y2) == null) ? 0 : qBinary.get(X, Y1, Y2);
  }

  private double q(String X, String w){
    return (qUnary.get(X, w) == null) ? 0 : qUnary.get(X, w);
  }

  public void train() throws IOException{
    readCounts();
    computeProbabilities();
  }

  public void classify() throws IOException{
    String line;
    while((line = testReader.readLine()) != null){
      String[] s = line.split(" ");

    }
  }

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_train.counts.out",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_dev.dat",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_dev.out"
    };

    CKYDecoder cky = new CKYDecoder(args[0], args[1], args[2]);
    cky.train();
  }
}