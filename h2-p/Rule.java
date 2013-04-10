/**
 * Represents a rule in the PCFG
 * X -> YZ 
 */
public class Rule {
  
  protected final String X;
  protected final String Y;
  protected final String Z;
  
  public final RuleType ruleType;
  
  public Rule(String X, String Y){
    this.X = X;
    this.Y = Y;
    this.Z = null;
    ruleType = RuleType.UNARYRULE;
  }
  
  public Rule(String X, String Y, String Z){
    this.X = X;
    this.Y = Y;
    this.Z = Z;
    ruleType = RuleType.BINARYRULE;
  }
  
  public enum RuleType{
    UNARYRULE,
    BINARYRULE
  }
  
  public boolean isUnaryRule(){
    return ruleType == RuleType.UNARYRULE;
  }
  
  public boolean isBinaryRule(){
    return ruleType == RuleType.BINARYRULE;
  }
  
  @Override
  public String toString(){
    return ""+X+" -> "+Y+", "+Z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((X == null) ? 0 : X.hashCode());
    result = prime * result + ((Y == null) ? 0 : Y.hashCode());
    result = prime * result + ((Z == null) ? 0 : Z.hashCode());
    result = prime * result + ((ruleType == null) ? 0 : ruleType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Rule other = (Rule) obj;
    if (X == null) {
      if (other.X != null)
        return false;
    } else if (!X.equals(other.X))
      return false;
    if (Y == null) {
      if (other.Y != null)
        return false;
    } else if (!Y.equals(other.Y))
      return false;
    if (Z == null) {
      if (other.Z != null)
        return false;
    } else if (!Z.equals(other.Z))
      return false;
    if (ruleType != other.ruleType)
      return false;
    return true;
  }
}