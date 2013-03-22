import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Solution to part-2 of the assignment.
 */
public class HMMTagger {

  private final static String   WORDTAG = "WORDTAG";
  private final static String[] tags    = {"O", "I-GENE"};
  private final static String   START   = "*";
  private final static String   STOP    = "STOP";

  //Readers , Writers to the training, test and the output file respectively
  private final BufferedReader corpusReader;
  private final BufferedReader testReader;
  private final PrintWriter    resultsWriter;

  public HMMTagger(String trainingFile, String testFile, String outputFile){
    try{
      corpusReader  = new BufferedReader(new FileReader(new File(trainingFile)));
      testReader    = new BufferedReader(new FileReader(new File(testFile)));
      resultsWriter = new PrintWriter(new File(outputFile));

    }catch(FileNotFoundException e){
      System.err.println("COULD NOT FIND FILE ...");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public void train() throws IOException{
    readCounts();
    computeTransitionProbabilities();
  }

  private void readCounts() throws IOException{
    String line;
    while((line = corpusReader.readLine()) != null){
      String[] str = line.split(" ");
      if(str[1].equals(WORDTAG)){continue;}

    }
    computeTransitionProbabilities();
    corpusReader.close();

  }

  private void computeTransitionProbabilities(){

  }


  private static void printUsage(){
    System.out.println("Expected 3 arguments \n java HMM <trainingFile> <testFile> <outputFile>");
  }

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene-rare.counts",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene.dev",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene_dev.p1.out"
    };

    if(args.length != 3){
      printUsage();
    }

    HMMTagger hmmTagger = new HMMTagger(args[0], args[1], args[2]);
    hmmTagger.train();
    //    hmmTagger.classify();
  }

}