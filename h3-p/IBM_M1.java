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
import java.util.TreeMap;

public class IBM_M1 {

  private final static String SEPARATOR = "_";
  private final static String NULL = "NULL";

  private final Map2K<String, String, Double> t;
  private final Map<String, Double> n;
  private final Map<String, Double> q;

  private final Map<String, Double> c_ef;
  private final Map<String, Double> c_e;
  private final Map<String, Double> c_jilm;
  private final Map<String, Double> c_ilm;

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
    n = new TreeMap<>();
    q = new HashMap<>();
    c_ef = new HashMap<>();
    c_e = new HashMap<>();
    c_jilm = new HashMap<>();
    c_ilm = new HashMap<>();    
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


  private void resetCounts(){
    c_ef.clear();
    c_e.clear();
    c_jilm.clear();
    c_ilm.clear();
  }


  private String getKey_q(int j, int i, int l, int m){
    return j+SEPARATOR+i+SEPARATOR+l+SEPARATOR+m;
  }

  private double q(int j, int i, int l, int m){
    if(q.get(getKey_q(j, i, l, m)) == null){
      q(j, i, l, m, 0.0);
    }
    return q.get(getKey_q(j, i, l, m));
  }

  private void q(int j, int i, int l, int m, double val){
    q.put(getKey_q(j, i, l, m), val);
  }


  private String getKey_ef(String e, String f){
    return e+SEPARATOR+f;
  }

  private double c(String e, String f){
    if(c_ef.get(getKey_ef(e, f)) == null){
      c(e, f, 0.0);
    }
    return c_ef.get(getKey_ef(e, f));
  }

  private void c(String e, String f, double val){
    c_ef.put(getKey_ef(e, f), val);
  }



  private String getKey_jilm(int j, int i, int l, int m){
    return j+SEPARATOR+i+SEPARATOR+l+SEPARATOR+m;
  }

  private double c(int j, int i, int l, int m){
    if(c_jilm.get(getKey_jilm(j, i, l, m)) == null){
      c(j, i, l, m, 0.0);
    }
    return c_jilm.get(getKey_jilm(j, i, l, m));
  }

  private void c(int j, int i, int l, int m, double val){
    c_jilm.put(getKey_jilm(j, i, l, m),  val);
  }



  private String getKey_ilm(int i, int l, int m){
    return i+SEPARATOR+l+SEPARATOR+m;
  }

  private double c(int i, int l, int m){
    if(c_ilm.get(getKey_ilm(i, l, m)) == null){
      c(i, l, m, 0.0);
    }
    return c_ilm.get(getKey_ilm(i, l, m));
  }

  private void c(int i, int l, int m, double val){
    c_ilm.put(getKey_ilm(i, l, m), val);
  }


  private double c(String e){
    if(c_e.get(e) == null){
      c(e, 0.0);
    }
    return c_e.get(e);
  }

  private void c(String e, double val){
    c_e.put(e, val);
  }

  private double t(String f, String e){
    return (t.get(f, e) == null) ? 0.0 : t.get(f, e);
  }

  private double n(String e){
    return (n.get(e) == null) ? 0.0 : n.get(e);
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

      n.put(e, (double)candidateForeignWords.size());
      candidateForeignWords.clear();
    }
    
    int nrOfUniqForeignWords = spanish_.size();
    spanish_.clear();
    english_.clear();
    double t_f_NULL = 1.0/(double)(nrOfUniqForeignWords);
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
          if(t.get(f, e) == null){
            t_fe = 1.0/n(e);
            t.put(f, e, t_fe);
          }
        }
        t.put(f, NULL, t_f_NULL);
      }
      en = corpusEnReader.readLine();
      es = corpusEsReader.readLine();      
    }
    n.clear(); //free up memory
  }

  /**
   * Computes the maximum likelihood parameters using the Expectation Maximization algorithm.
   */
  public void train() throws IOException{
    initTranslationParameters();
    int nrOfIterations = 5;

    for(int iter = 0; iter < nrOfIterations; iter++){
      corpusEnReader.reset();
      corpusEsReader.reset();
      String english = corpusEnReader.readLine();
      String spanish = corpusEsReader.readLine();
      while(english != null){
        List<String> en = asList(english);
        List<String> es = asList(spanish);
        en.add(0, NULL);

        int m = es.size();
        int l = en.size();
        for(int i = 0; i < m; i++){
          for(int j = 0; j < l; j++){

            String e = en.get(j);
            String f = es.get(i);

            double num = t(f, e);

            double denom = 0;
            for(int j_ = 0; j_ < l; j_++){
              String e_ = en.get(j_);
              denom += t(f, e_);
            }
            double del = num/denom;
            c(e, f, c(e, f) + del);                 //c(e,f) = c(e,f) + del(k,i,j)
            c(e, c(e) + del);                       //c(e) = c(e) + del(k,i,j)
            c(j, i, l, m, c(j, i, l, m) + del);     //c(j | i,l,m) = c(j | i,l,m) + del(k,i,j)
            c(i, l, m, c(i, l, m) + del);           //c(i, l, m) = c(i, l, m) + del(k, i, j)
          }
        }
        english = corpusEnReader.readLine();
        spanish = corpusEsReader.readLine();
      }

      //Update t's and q's
      corpusEnReader.reset();
      corpusEsReader.reset();
      String en = corpusEnReader.readLine();
      String es = corpusEsReader.readLine();
      double t_fe = 0.0d;
      while(en != null){
        List<String> en_ = asList(en);
        List<String> es_ = asList(es);
        en_.add(0, NULL);

        int m = es_.size();
        int l = en_.size();

        for(int i = 0; i < m; i++){
          for(int j = 0; j < l; j++){
            String e = en_.get(j);
            String f = es_.get(i);
            t_fe = c(e, f)/c(e);
            t.put(f, e, t_fe);
            q(j,i,l,m,  c(j, i, l, m)/c(i, l, m));
          }
        }
        en = corpusEnReader.readLine();
        es = corpusEsReader.readLine();
      }
      resetCounts();
    }
    corpusEnReader.close();
    corpusEsReader.close();
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