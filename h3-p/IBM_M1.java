import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IBM_M1 {

  private final static String NULL = "NULL";
  private final static Charset charset = Charset.forName("UTF-8");
  private final static CharsetDecoder decoder = charset.newDecoder();
  
  private final Map2K<String, String, Double> t;
  private final Map<String, Integer> n;

  private MemoryMappedFileReader corpusEnReader;
  private MemoryMappedFileReader corpusEsReader;
  private BufferedReader testEnReader;
  private BufferedReader testEsReader;
  private PrintWriter    resultsWriter;

  private final String corpusEnFile;
  private final String corpusEsFile;

  public IBM_M1(String corpusEnFile, String corpusEsFile, String testEnFile,
      String testEsFile, String outputFile){
    t = new Map2K<>();
    n = new HashMap<>();
    this.corpusEnFile = corpusEnFile;
    this.corpusEsFile = corpusEsFile;
    setupIO(corpusEnFile, corpusEsFile, testEnFile, testEsFile, outputFile);
  }

  private void setupIO(String corpusEnFile, String corpusEsFile, String testEnFile,
      String testEsFile, String outputFile){
    try {
      corpusEnReader = new MemoryMappedFileReader(corpusEnFile);
      corpusEsReader = new MemoryMappedFileReader(corpusEsFile);
      testEnReader = new BufferedReader(new FileReader(testEnFile));
      testEsReader = new BufferedReader(new FileReader(testEsFile));
      resultsWriter = new PrintWriter(new File(outputFile), "UTF-8");
      
    } catch (IOException e) {
      System.err.println("Could not setup IO");
      e.printStackTrace();
    }
  }

  private double t(String f, String e){
    return (t.get(f, e) == null) ? 0 : t.get(f, e);
  }

  private double n(String e){
    return (n.get(e) == null) ? 0 : n.get(e);
  }

  private Set<String> asSet(String line){
    Set<String> set = new HashSet<>();
    for(String word : line.split(" ")){
      set.add(word);
    }
    return set;
  }
  
  private List<String> asList(String line){
    List<String> list = new LinkedList<>();
    for(String word : line.split(" ")){
      list.add(word);
    }
    return list;
  }

  private void initTranslationParameters() throws IOException{
    String en = corpusEnReader.readLine();
    String es = corpusEsReader.readLine();
    Set<String> english_ = new HashSet<>(); //all unique English words
    Set<String> spanish_ = new HashSet<>(); //all unique Spanish words
    Set<String> candidateForeignWords = new HashSet<>();
    while(en != null){
      for(String e : en.split(" ")){
        english_.add(e);
      }
      for(String f : es.split(" ")){
        spanish_.add(f);
      }
      en = corpusEnReader.readLine();
      es = corpusEsReader.readLine();
    }
    
    System.out.println("phase - 0 over");
    System.out.println(english_.size());
    System.out.println(spanish_.size());

    for(String e : english_){
      corpusEnReader.reset();
      corpusEsReader.reset();
      en = corpusEnReader.readLine();
      es = corpusEsReader.readLine();

      while(en != null){
        Set<String> en_ = asSet(en);
        Set<String> es_ = asSet(es);
        if(en_.contains(e)){
          candidateForeignWords.addAll(es_);
        }
        en = corpusEnReader.readLine();
        es = corpusEsReader.readLine();
      }

      n.put(e, candidateForeignWords.size());
      candidateForeignWords.clear();
    }
    
    int nrOfUniqForeignWords = spanish_.size();
    double t_f_NULL = 1.0/(double)(nrOfUniqForeignWords);
    spanish_.clear();
    english_.clear();
    corpusEnReader.reset();
    corpusEsReader.reset();
    en = corpusEnReader.readLine();
    es = corpusEsReader.readLine();

    double t_fe = 0.0d;
    while(en != null){
      Set<String> en_ = asSet(en);
      Set<String> es_ = asSet(es);
      for(String f : es_){
        for(String e : en_){
          t_fe = 1/n(e);
          t.put(f, e, t_fe);
        }
        t.put(f, NULL, t_f_NULL);
      }
      en = corpusEnReader.readLine();
      es = corpusEsReader.readLine();      
    }
  }

  /**
   * Computes the maximum likelihood parameters using the Expectation Maximization algorithm.
   */
  public void train() throws IOException{
    initTranslationParameters();
    corpusEnReader.reset();
    corpusEsReader.reset();
    int nrOfIterations = 5;
    
    for(int iter = 0; iter < 5; iter++){
      String english = corpusEnReader.readLine();
      String spanish = corpusEsReader.readLine();
      while(english != null){
        List<String> en = asList(english);
        List<String> es = asList(spanish);
        
        int m_k = es.size();
        int l_k = en.size();
        for(int i = 0; i < m_k; i++){
          for(int j = 0; j < l_k; j++){
            double del_k_i_j = t(es.get(i), en.get(j));
            //TODO: do stuff here.
          }
        }
        english = corpusEnReader.readLine();
        spanish = corpusEsReader.readLine();
      }
      corpusEnReader.close();
      corpusEsReader.close();      
    }
  }


  public static void main(String[] args) throws IOException {
    args = new String[]{
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h3-p/corpus.en",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h3-p/corpus.es",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h3-p/dev.en",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h3-p/dev.es",
        "/home/dapurv5/MyCode/coursera-projects/coursera-nlangp/h3-p/dev.out"
    };

    IBM_M1 ibmm1 = new IBM_M1(args[0], args[1], args[2], args[3], args[4]);
    ibmm1.train();
  }
}