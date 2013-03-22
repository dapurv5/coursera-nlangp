import java.util.HashMap;
import java.util.Map;


public class MultiKeyMap<KEY1, KEY2, VALUE> {

  private final Map<KEY1, Map<KEY2, VALUE>> m1 = new 
      HashMap<KEY1, Map<KEY2, VALUE>>();
  
  public void put(KEY1 k1, KEY2 k2, VALUE v){
    Map<KEY2, VALUE> m2 = m1.get(k1);
    if(m2 == null){
      m2 = new HashMap<KEY2, VALUE>();
      m1.put(k1, m2);
    }
    m2.put(k2, v);
  }

  public VALUE get(KEY1 k1, KEY2 k2){
    return (m1.get(k1) == null)?null : m1.get(k1).get(k2);
  }
  
  public boolean containsKey(KEY1 k1, KEY2 k2){
    return m1.get(k1) != null && m1.get(k1).get(k2) != null;
  }  
}