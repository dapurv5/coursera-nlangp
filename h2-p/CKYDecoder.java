import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.LinkedMap;


/**
 * Solution to part-2 of the assignment.
 */
public class CKYDecoder {

  private final Map<Rule, Integer>   cntRules;
  private final Map<Integer, String> indexNonterminals;
  private final Map<String, Integer> invIndexNonterminals;
  private final Map<String, Integer> cntNonterminals;
  private final Map<Rule, Double>    q;
  private final Set<String>          seenWords;
  private int index = 0;
  
  private final Map<String, List<Rule>> rules;

  private BufferedReader trainingReader;
  private BufferedReader testReader;
  private PrintWriter    resultsWriter;

  private double[][][] P;  //Pi
  private Rule[][][] bp_R; //backptr to store the rules
  private int[][][] bp_s;  //backptr to store the split point.

  public CKYDecoder(String trainingFile, String testFile, String outputFile){
    cntRules             = new HashMap<>();
    rules                = new HashMap<>();
    cntNonterminals      = new HashMap<>();
    q                    = new HashMap<>();    
    seenWords            = new HashSet<String>();
    indexNonterminals    = new HashMap<>();
    invIndexNonterminals = new HashMap<>();
    setupIO(trainingFile, testFile, outputFile);
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
          rules.put(tokens[2], new ArrayList<Rule>());
          indexNonterminals.put(index, tokens[2]);
          invIndexNonterminals.put(tokens[2], index);
          index++;
          break;
          
        case "UNARYRULE":
          seenWords.add(tokens[3]);
          Rule unaryRule = new Rule(tokens[2], tokens[3]);
          cntRules.put(unaryRule, Integer.parseInt(tokens[0]));
          List<Rule> list2 = rules.get(tokens[2]);
          list2.add(unaryRule);
          break;
          
        case "BINARYRULE":
          Rule binaryRule = new Rule(tokens[2], tokens[3], tokens[4]);
          cntRules.put(binaryRule, Integer.parseInt(tokens[0]));
          List<Rule> list3 = rules.get(tokens[2]);
          list3.add(binaryRule);
          break;
      }
    }
    trainingReader.close();
  }

  
  private void computeProbabilities() throws IOException{
    double d = 0.0d;
    for(String X : rules.keySet()){
      for(Rule rule : rules.get(X)){
        d = 0.0d;
        d = (double)cntRules.get(rule)/(double)cntNonterminals.get(X);
        q.put(rule, d);
      }
    }
  }

  private double q(String X, String Y1, String Y2){
    Rule rule = new Rule(X, Y1, Y2);
    return (q.get(rule) == null)? 0 : q.get(rule);
  }
  

  private double q(String X, String w){
    Rule rule = new Rule(X,w);

    if(q.get(rule) != null){
      return q.get(rule);
    }
    if(seenWords.contains(w)){
      return 0.0d;
    }
    if(q.get(new Rule(X, "_RARE_")) != null){
      return q.get(new Rule(X, "_RARE_"));
    }
    return 0.0d;
  }

  public void train() throws IOException{
    readCounts();
    computeProbabilities();
  }

  public void parse() throws IOException{
    String line;
    while((line = testReader.readLine()) != null){
      line = "What are geckos ?";
      String[] s = line.split(" ");
      System.out.println(Arrays.toString(s));
      ArrayList<Object> tree = cky(s);
      System.exit(0);
    }
    testReader.close();
  }


  /**
   * Given a sentence and a PCFG, CKY algorithm returns the most probable parse
   * tree for the sentence.
   */
  private ArrayList<Object> cky(String[] str){
    int n = str.length;
    int R = indexNonterminals.size();

    P = new double[n][n][R];
    bp_R = new Rule[n][n][R];
    bp_s = new int[n][n][R];

    for(int i = 0; i < n; i++){
      for(int x = 0; x < R; x++){
        String X = indexNonterminals.get(x);
        P[i][i][x] = q(X, str[i]);
        if(P[i][i][x] != 0.0d){
          bp_s[i][i][x] = i;
          bp_R[i][i][x] = new Rule(X, str[i]);
        }
      }
    }

    for(int l = 1; l <= n-1; l++){
      for(int i = 0; i <= n-l-1 ; i++){
        int j = i+l;

        for(int x = 0; x < R; x++){
          String X = indexNonterminals.get(x);
          double maxPi = Double.MIN_VALUE;
          int maxS = -1;
          Rule maxRule = null;

          for(int s = i; s <= j-1; s++){
            for(Rule rule:rules.get(X)){
              if(rule.isUnaryRule()){continue;}
              String Y = rule.Y;
              String Z = rule.Z;
              int y = invIndexNonterminals.get(Y);
              int z = invIndexNonterminals.get(Z);
              
              double res = q(X, Y, Z)*P[i][s][y]*P[s+1][j][z];
              
              if(res > maxPi){
                maxPi = res;
                maxS = s;
                maxRule = rule;
              }
            }
          }

          P[i][j][x]      = maxPi;
          bp_R[i][j][x]   = maxRule;
          bp_s[i][j][x]   = maxS;
        }
      }
    }
//    System.out.println(bp_R[1][n-1][invIndexNonterminals.get("SBARQ")]);
    return constructTree(str, 0, n-1, invIndexNonterminals.get("SBARQ"));
  }

  private ArrayList<Object> constructTree(String[] s, int i, int j, int x){
    int n = P.length;
    ArrayList<Object> tree = new ArrayList<>();
    if(i == j){
      ArrayList<Object> leaf = new ArrayList<>();
      leaf.add(s[i]);
      leaf.add(indexNonterminals.get(x));
      return leaf;
    }
    return tree;
  }

  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_train.counts.out",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_dev.dat",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h2-p/parse_dev.out"
    };

    CKYDecoder cky = new CKYDecoder(args[0], args[1], args[2]);
    cky.train();
    cky.parse();
  }
}