/**
 *  Copyright (c) 2013 Apurv Verma
 */
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;

public class ByteBufferReader extends Reader{
  
  private final Reader reader;

  public ByteBufferReader(ByteBuffer byteBuffer){
    this.reader = new InputStreamReader(new ByteBufferInputStream(byteBuffer));
  }
  
  /* (non-Javadoc)
   * @see java.io.Reader#close()
   */
  @Override
  public void close() throws IOException {
    reader.close();
  }

  /* (non-Javadoc)
   * @see java.io.Reader#read(char[], int, int)
   */
  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    return reader.read(cbuf, off, len);
  }
}