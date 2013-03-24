import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Solution to part-2 of the assignment.
 */
public class ViterbiTagger {

  private final static String   WORDTAG = "WORDTAG";
  private final static String[] tags    = {"*", "O", "I-GENE", "STOP"};
  private final static int      START   = 0;
  private final static int      STOP    = 3; 

  private final static String NUMERIC = "_NUMERIC_";
  private final static String ALLCAPITALS = "_ALLCAPITALS_";
  private final static String LASTCAPITAL = "_LASTCAPITAL";
  private final static String RARE = "_RARE_";

  //Readers , Writers to the training, test and the output file respectively
  private final BufferedReader corpusReader;
  private final BufferedReader testReader;
  private final PrintWriter    resultsWriter;

  private final Map<String, Integer>           tagCnts       = new HashMap<String, Integer>();
  private final Map2K<String, String, Integer> emissionCnts  = new Map2K<String, String, Integer>();
  private final Map2K<String, String, Double>  emissionProbs = new Map2K<String, String, Double>();
  private final List<String>                   allwords      = new LinkedList<String>();

  private final Map<String, Integer>                   gram_1_Cnts = new HashMap
      <String, Integer>();
  private final Map2K<String, String, Integer>         gram_2_Cnts = new Map2K
      <String, String, Integer>();
  private final Map3K<String, String, String, Integer> gram_3_Cnts = new Map3K
      <String, String, String, Integer>();
  private final Map3K<String, String, String, Double> transitionProbs = new Map3K
      <String, String, String, Double>(); //q(s|u,v)

  public ViterbiTagger(String trainingFile, String testFile, String outputFile){
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
    computeEmissionProbabilities();
    computeTransitionProbabilities();
  }

  private void readCounts() throws IOException{
    String line;
    while((line = corpusReader.readLine()) != null){
      String[] str = line.split(" ");
      if(str[1].equals(WORDTAG)){
        String word    = str[3];
        String tag     = str[2];
        int freq       = Integer.parseInt(str[0]);
        emissionCnts.put(tag, word, freq);
        allwords.add(word);

        Integer cntTag = (tagCnts.get(tag) == null)?0 : tagCnts.get(tag);
        tagCnts.put(tag, cntTag+freq);
      } else{
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

    }
    corpusReader.close();
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

  public double e(String x, String y){
    if (emissionProbs.containsKey(x, y)){
      return emissionProbs.get(x, y);
    }

    //x is a new word. 
    if(StringUtils.isNumeric(x)){
      return emissionProbs.get(NUMERIC, y);
    }
    else if(StringUtils.isAllUpperCase(x)){
      return emissionProbs.get(ALLCAPITALS, y);
    }
    else if(Character.isUpperCase(x.charAt(x.length()-1))){
      return emissionProbs.get(LASTCAPITAL, y);
    }
    else{
      return emissionProbs.get(RARE, y);
    }
  }

  public double q(String s, String u, String v){
    return transitionProbs.get(s, u, v);
  }

  private double q(int s, int u, int v){
    return transitionProbs.get(tags[s], tags[u], tags[v]);
  }

  private double e(String x, int y){
    return e(x, tags[y]);
  }

  public String[] viterbi(String[] x){
    int[] y_index = new int[x.length];
    int K = tags.length;
    int n = x.length;
    double max = Double.MIN_VALUE;
    double[][][] P = new double[n+1][K][K];
    int[][][] bp   = new int[n+1][K][K]; //backpointers
    P[0][0][0] = 1.0d;
    for(int k = 1; k <= n; k++){
      for(int u = 0; u < K; u++){
        for(int v = 0; v < K; v++){
          max = 0;
          int w_max = 0;
          for(int w = 0; w < K; w++){
            //            System.out.print("q("+tags[v]+","+tags[w]+","+tags[u]+")="+q(v,w,u)+"  ");
            if(max < P[k-1][w][u] * q(v,w,u) * e(x[k-1], v)){
              max = P[k-1][w][u] * q(v,w,u) * e(x[k-1], v);
              w_max = w;
              bp[k][u][v] = w;
            }
          }
          P[k][u][v] = max;
          //          System.out.println();
          //          System.out.println("Double.min value = "+Double.MIN_VALUE);
          //          System.out.println("For k = "+k);
          //          System.out.println("u = "+tags[u]);
          //          System.out.println("v = "+tags[v]);
          //          System.out.println("w_max is = "+tags[w_max]);
          //          System.out.println("x["+(k-1)+"] is "+x[k-1]);
          //          System.out.println("e["+x[k-1]+" | "+tags[v]+"] = "+e(x[k-1], v)+"  and "+
          //              "e["+x[k-1]+" | "+tags[0]+"] = "+e(x[k-1], 0)+" ,"+
          //              "e["+x[k-1]+" | "+tags[1]+"] = "+e(x[k-1], 1)+", "+
          //              "e["+x[k-1]+" | "+tags[2]+"] = "+e(x[k-1], 2)+", "+
          //              "e["+x[k-1]+" | "+tags[3]+"] = "+e(x[k-1], 3)
          //              );
          //          System.out.println("P["+k+","+tags[u]+","+tags[v]+"] = "+P[k][u][v]);
          //          System.out.println("bp["+k+","+tags[u]+","+tags[v]+"] = "+tags[bp[k][u][v]]);
          //          System.out.println("\n");
        }
      }
    }

    max = Double.MIN_VALUE;
    for(int u = 0; u < K; u++){
      for(int v = 0; v < K; v++){
        if(max < P[n][u][v]*q(STOP, u, v)){
          max = P[n][u][v]*q(STOP, u, v);
          y_index[n-1] = v;
          if(n-2 >= 0){
            y_index[n-2] = u; 
          }
        }
      }
    }

    for(int k = n-3; k>=0; k--){
      //      System.out.println("y["+k+"] = bp["+(k+3)+","+tags[yI[k+1]]+","+tags[yI[k+2]]+"] = "
      //    +tags[bp[k+3][yI[k+1]][yI[k+2]]]);
      y_index[k] = bp[k+3][y_index[k+1]][y_index[k+2]];
    }

    String[] y= new String[n];
    for(int i = 0; i < n; i++){
      y[i] = tags[y_index[i]];
    }
    //    System.out.println(Arrays.toString(y));
    return y;
  }

  private void classify() throws IOException{
    String line;
    List<String> sentence = new LinkedList<String>();
    while((line = testReader.readLine()) != null){
      String word = line.trim();
      if(word.equals("")){
        String[] y = viterbi(sentence.toArray(new String[sentence.size()]));
        for(int i = 0; i < sentence.size(); i++){
          resultsWriter.println(sentence.get(i)+" "+y[i]);
        }
        resultsWriter.println();
        sentence.clear();
      } else{
        sentence.add(word);
      }
    }
    testReader.close();
    resultsWriter.close();
  }

  private static void printUsage(){
    System.out.println("Expected 3 arguments \n java HMM <trainingFile> <testFile> <outputFile>");
  }

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene-rare.counts",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene.dev",
        "/home/dapurv5/MyCode/private-projects/nlangp-assignments/h1-p/gene_dev.p3.out"
    };

    if(args.length != 3){
      printUsage();
    }

    ViterbiTagger hmmTagger = new ViterbiTagger(args[0], args[1], args[2]);
    hmmTagger.train();
    hmmTagger.classify();
  }
}