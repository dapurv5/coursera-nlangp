import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * Solution for Part-1 of the assignment
 * Estimates the parameters for Trigram Hidden Markov model.</br>
 */
public class HMM {

  private final static String WORDTAG = "WORDTAG";
  private final static String[] tags = {"O", "I-GENE"};

  //Readers , Writers to the training, test and the output file respectively
  private final BufferedReader trainingReader;
  private final BufferedReader testReader;
  private final PrintWriter    resultsWriter;

  //Data structures to hold count and probabilities
  private final Map<String, Integer>                tagCnts       = new HashMap<String, Integer>();
  private final MultiKeyMap<String, String, Integer>emissionCnts  = new MultiKeyMap<>();
  private final MultiKeyMap<String, String, Double> emissionProbs = new MultiKeyMap<>();
  private final List<String>                        allwords      = new LinkedList<String>();

  public HMM(String trainingFile, String testFile, String outputFile){
    try{
      trainingReader = new BufferedReader(new FileReader(new File(trainingFile)));
      testReader     = new BufferedReader(new FileReader(new File(testFile)));
      resultsWriter  = new PrintWriter(new File(outputFile));

    }catch(FileNotFoundException e){
      System.err.println("COULD NOT FIND FILE ...");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private void readCounts() throws IOException{
    String line;
    while((line = trainingReader.readLine()) != null){
      String[] str = line.split(" ");
      if(str[1].equals(WORDTAG)){
        String word    = str[3];
        String tag     = str[2];
        int freq       = Integer.parseInt(str[0]);
        emissionCnts.put(tag, word, freq);
        allwords.add(word);

        Integer cntTag = (tagCnts.get(tag) == null)?0 : tagCnts.get(tag);
        tagCnts.put(tag, cntTag+freq);
      }
    }
    computeEmissionProbabilities();
    trainingReader.close();
  }

  private void computeEmissionProbabilities(){
    for(String x: allwords){
      for(String y:tags){
        if(emissionCnts.get(y, x) == null){
          emissionProbs.put(x, y, 0.0d);
        }else{
          double cnt_yx = emissionCnts.get(y, x);
          double cnt_y  = tagCnts.get(y); 
          double prob = cnt_yx / cnt_y;
          emissionProbs.put(x, y, prob);
        }
      }
    }
  }

  /**
   * Returns the pre-computed emission probabilities.
   * @param y the tag  {0, I-GENE}
   * @param x the word {TFII, ... etc}
   */
  public double e(String x, String y){
    if (emissionProbs.containsKey(x, y)){
      return emissionProbs.get(x, y);
    }
    return emissionProbs.get("_RARE_", y);//x is a new word.
  }

  public void train() throws IOException{
    readCounts();
    computeEmissionProbabilities();
  }

  public void classify() throws IOException{
    String line;
    while((line = testReader.readLine()) != null){
      String word = line.trim();
      if(word.equals("")){
        resultsWriter.println();
        continue;
      }
      String maxTag = tags[0];
      double maxEmissionProb = Double.MIN_VALUE;
      for(String tag:tags){
        if( e(word, tag) > maxEmissionProb){
          maxEmissionProb = e(word, tag);
          maxTag = tag;
        }
      }
      resultsWriter.println(word+" "+maxTag);
    }
    resultsWriter.close();
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

    HMM hmm = new HMM(args[0], args[1], args[2]);
    hmm.train();
    hmm.classify();
  }
}