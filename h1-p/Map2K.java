import java.util.HashMap;
import java.util.Map;


public class Map2K<KEY1, KEY2, VALUE> {

  private final Map<KEY1, Map<KEY2, VALUE>> m1 = new 
      HashMap<KEY1, Map<KEY2, VALUE>>();
  
  
  public static class MultiKey<KEY1, KEY2>{
    KEY1 key1;
    KEY2 key2;
    
    public MultiKey(KEY1 key1, KEY2 key2){
      this.key1 = key1;
      this.key2 = key2;
    }
    
    public KEY1 getKey1(){
      return key1;
    }
    
    public KEY2 getKey2(){
      return key2;
    }
    
    @Override
    public String toString(){
      return "("+key1+","+key2+")";
    }
  }
  
  
  public void put(KEY1 k1, KEY2 k2, VALUE val){
    Map<KEY2, VALUE> m2 = m1.get(k1);
    if(m2 == null){
      m2 = new HashMap<KEY2, VALUE>();
      m1.put(k1, m2);
    }
    m2.put(k2, val);
  }

  public VALUE get(KEY1 k1, KEY2 k2){
    return (m1.get(k1) == null)?null : m1.get(k1).get(k2);
  }
  
  public boolean containsKey(KEY1 k1, KEY2 k2){
    return m1.get(k1) != null && m1.get(k1).get(k2) != null;
  }
  
  @Override
  public String toString(){
    String str = "{";
    for(KEY1 key1:m1.keySet()){
      Map<KEY2, VALUE> m2 = m1.get(key1);
      for(KEY2 key2:m2.keySet()){
        str = str + "("+key1+","+key2+")=" +m2.get(key2) +", ";
      }
    }
    str = str.substring(0, str.length()-2) + "}";
    return str;
  }
}