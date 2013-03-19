import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;

/**
 * Estimates the parameters for Trigram Hidden Markov model.</br>
 */
public class HMM {
  
  private final static String WORDTAG = "WORDTAG";
  private final static String I_GENE  = "I-GENE";
  
  Map<String, Integer> tagCounts;
  MultiKeyMap emissionCounts;
  MultiKeyMap emissionProbabilities;
  
  public HMM(){
    tagCounts = new HashMap<String, Integer>();
    emissionCounts = new MultiKeyMap();
    emissionProbabilities = new MultiKeyMap();
  }
  
  private void readCounts() throws IOException{
    InputStream in = getClass().getResourceAsStream("gene.counts");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line;
    while((line = reader.readLine()) != null){
      String[] str = line.split(" ");
      if(str[1].equals(WORDTAG)){
        String word    = str[3];
        String tag     = str[2];
        int freq       = Integer.parseInt(str[0]);
        emissionCounts.put(tag, word, freq);
        
        Integer cntTag = (tagCounts.get(tag) == null)?0 : tagCounts.get(tag);
        tagCounts.put(tag, cntTag+1);
      }
    }
    computeEmissionProbabilities();
  }
  
  private void computeEmissionProbabilities(){
    for(Object k: emissionCounts.keySet()){
      MultiKey key = (MultiKey)k;
      String y = (String)key.getKey(0); //the tag
      String x = (String)key.getKey(1); //the word
      double prob = ((double)(int)emissionCounts.get(y, x))/((double)(int)tagCounts.get(y));
      emissionProbabilities.put(x, y, prob);
    }
  }
  
  /**
   * Returns the precomputed emission probabilities.
   * @param y the tag  {0, I-GENE}
   * @param x the word {Galphao, TFII, ... etc}
   */
  public double e(String x, String y){
    return (int)emissionProbabilities.get(x, y);
  }
  
  public static void main(String[] args) throws IOException {
    HMM hmm = new HMM();
    hmm.readCounts();
  }

}