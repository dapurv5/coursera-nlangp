/**
 *  Copyright (c) 2013 Apurv Verma
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileReader{
  
  private final ByteBuffer byteBuffer;
  private ByteBufferReader bbr;
  private BufferedReader reader;
  
  public MemoryMappedFileReader(String filename){
    try {
      RandomAccessFile raf = new RandomAccessFile(filename, "r");
      MappedByteBuffer mbb = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
      byteBuffer = mbb.asReadOnlyBuffer();
      byteBuffer.mark();
      init();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to initialize MemoryMappedFileReader...", e);
    }
  }
  
  private final void init(){
    bbr = new ByteBufferReader(byteBuffer);
    reader = new BufferedReader(bbr);
  }
  
  public String readLine() throws IOException{
    return reader.readLine();
  }
  
  public void reset(){
    byteBuffer.reset();
    init();
  }

  public void close() throws IOException {
    reader.close();
  }
}