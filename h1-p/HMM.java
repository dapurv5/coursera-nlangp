import java.util.HashMap;
import java.util.Map;

/**
 * Estimates the parameters for Trigram Hidden Markov model.</br>
 */
public class HMM {
  
  Map<Duple<String, String>, Integer> emissionCounts;
  
  public HMM(){
    emissionCounts = new HashMap<>();
  }
  
  private void readCounts(){
    
  }
  
  public static void main(String[] args) {

  }

}