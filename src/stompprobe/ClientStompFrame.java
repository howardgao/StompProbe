package org.hornetq.test.stomp.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

//pls use factory to create frames.
public interface ClientStompFrame
{

   public ByteBuffer toByteBuffer() throws UnsupportedEncodingException;

   public boolean needsReply();

   public void setCommand(String command);

   public void addHeader(String string, String string2);

   public void setBody(String string);

   public String getCommand();

   public String getHeader(String header);

   public String getBody();

   public ByteBuffer toByteBufferWithExtra(String str)  throws UnsupportedEncodingException;
   
   public boolean isPing();
      
}
