import java.util.HashMap;
import java.util.Map;


public class Map3K<KEY1, KEY2, KEY3, VALUE> {

  private final Map<KEY1, Map<KEY2, Map<KEY3, VALUE>>> m1 = new 
      HashMap<KEY1, Map<KEY2, Map<KEY3, VALUE>>>();

  public void put(KEY1 k1, KEY2 k2, KEY3 k3, VALUE val){
    Map<KEY2, Map<KEY3, VALUE>> m2 = m1.get(k1);
    if(m2 == null){
      m2 = new HashMap<KEY2, Map<KEY3, VALUE>>();
      m1.put(k1, m2);
    }
    Map<KEY3, VALUE> m3 = m2.get(k2);
    if(m3 == null){
      m3 = new HashMap<KEY3, VALUE>();
      m2.put(k2, m3);
    }
    m3.put(k3, val);
  }

  public VALUE get(KEY1 k1, KEY2 k2, KEY3 k3){
    return (m1.get(k1) == null)?null : 
      ((m1.get(k1).get(k2) == null)?null: m1.get(k1).get(k2).get(k3));
  }

  public boolean containsKey(KEY1 k1, KEY2 k2, KEY3 k3){
    return m1.get(k1) != null && m1.get(k1).get(k2) != null 
        && m1.get(k1).get(k2).get(k3) != null;
  }

  @Override
  public String toString(){
    String str = "{";
    for(KEY1 key1:m1.keySet()){
      Map<KEY2, Map<KEY3, VALUE>> m2 = m1.get(key1);
      for(KEY2 key2:m2.keySet()){
        Map<KEY3, VALUE> m3 = m2.get(key2);
        for(KEY3 key3:m3.keySet()){
          str = str + "("+key1+","+key2+","+key3+")=" +m3.get(key3) +", ";
        }
      }
    }
    str = str.substring(0, str.length()-2) + "}";
    return str;
  }
}