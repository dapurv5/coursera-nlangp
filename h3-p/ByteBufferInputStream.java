/**
 *  Copyright (c) 2013 Apurv Verma
 */


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream{
  
  private final ByteBuffer byteBuffer;

  /** Creates an uninitialized stream that cannot be used until 
   * {@link #setByteBuffer(ByteBuffer)} is called. 
   */
  public ByteBufferInputStream (ByteBuffer byteBuffer) {
    this.byteBuffer = byteBuffer;
  }

  public ByteBuffer getByteBuffer () {
    return byteBuffer;
  }

  @Override
  public int read() throws IOException {
    if (!byteBuffer.hasRemaining()) {return -1;}
    return byteBuffer.get();
  }
}