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
public class HMMTagger {

  private final static String   WORDTAG = "WORDTAG";
  private final static String[] tags    = {"*", "O", "I-GENE", "STOP"};
  private final static String   START   = "*";
  private final static String   STOP    = "STOP";

  //Readers , Writers to the training, test and the output file respectively
  private final BufferedReader corpusReader;
  private final BufferedReader testReader;
  private final PrintWriter    resultsWriter;

  private final Map<String, Integer>                   gram_1_Cnts = new HashMap
      <String, Integer>();
  private final Map2K<String, String, Integer>         gram_2_Cnts = new Map2K
      <String, String, Integer>();
  private final Map3K<String, String, String, Integer> gram_3_Cnts = new Map3K
      <String, String, String, Integer>();
  private final Map3K<String, String, String, Double> transitionProbs = new Map3K
      <String, String, String, Double>(); //q(s|u,v)

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

      int cnt = Integer.parseInt(str[0]);
      switch(str[1]){
        case "1-GRAM":
          gram_1_Cnts.put(str[2], cnt);
          break;
        case "2-GRAM":
          gram_2_Cnts.put(str[2], str[3], cnt);
          break;
        case "3-GRAM":
          gram_3_Cnts.put(str[2], str[3], str[4], cnt);
          break;
        default:
          break;
      }
      
    }
    System.out.println(gram_1_Cnts);
    System.out.println(gram_2_Cnts);
    System.out.println(gram_3_Cnts);
    corpusReader.close();
    computeTransitionProbabilities();
    System.out.println(transitionProbs);
  }

  private void computeTransitionProbabilities(){
    for(String u:tags){
      for(String v:tags){
        for(String s:tags){
          if(gram_3_Cnts.get(u, v, s) == null){
            transitionProbs.put(s, u, v, 0.0d);
          } else{
            double cnt_uvs = gram_3_Cnts.get(u, v, s);
            double cnt_uv  = gram_2_Cnts.get(u, v);
            double prob    = cnt_uvs/cnt_uv;
            transitionProbs.put(s, u, v, prob);
          }
        }
      }
    }
  }

  public double q(String s, String u, String v){
    return transitionProbs.get(s, u, v);
  }

  private static void printUsage(){
    System.out.println("Expected 3 arguments \n java HMM <trainingFile> <testFile> <outputFile>");
  }

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene-rare.counts",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene.dev",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene_dev.p2.out"
    };

    if(args.length != 3){
      printUsage();
    }

    HMMTagger hmmTagger = new HMMTagger(args[0], args[1], args[2]);
    hmmTagger.train();
    //    hmmTagger.classify();
  }

}