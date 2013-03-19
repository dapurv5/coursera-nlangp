/**
 *  Copyright (c) 2011 Apurv Verma
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 *  limitations under the License.
 */


/**
 * Container to hold a pair of values.
 * Note: Hashcode is content dependent.
 * For definition of content independence. 
 * {@link KeyValuePair#KeyValuePair()}
 */
public class Duple<X, Y> {

  private X x;
  private Y y;

  public Duple(){}

  public Duple(X x, Y y){
    this.x = x;
    this.y = y;
  }

  public X getX() {
    return x;
  }

  public Duple<X,Y> setX(X x) {
    this.x = x;
    return this;
  }

  public Y getY() {
    return y;
  }

  public Duple<X,Y> setY(Y y) {
    this.y = y;
    return this;
  }

  @Override
  public int hashCode() {
    return (x.hashCode() * 31) + y.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj){return true;}
    if(getClass() != obj.getClass()){return false;}
    Duple that = (Duple)obj;
    return this.x == that.x && this.y == that.y;
  }

  @Override
  public String toString() {
    return "("+x+", "+y+")";
  }
}