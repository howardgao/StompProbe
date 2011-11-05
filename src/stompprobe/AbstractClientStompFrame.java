package org.hornetq.test.stomp.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractClientStompFrame implements ClientStompFrame
{
   protected static final String HEADER_RECEIPT = "receipt";
   
   protected String command;
   protected List<Header> headers = new ArrayList<Header>();
   protected Set<String> headerKeys = new HashSet<String>();
   protected String body;

   public AbstractClientStompFrame(String command)
   {
      this.command = command;
   }

   @Override
   public boolean isPing()
   {
      return false;
   }

   public String toString()
   {
      StringBuffer sb = new StringBuffer("Frame: <" + command + ">" + "\n");
      Iterator<Header> iter = headers.iterator();
      while (iter.hasNext())
      {
         Header h = iter.next();
         sb.append(h.key + ":" + h.val + "\n");
      }
      sb.append("\n");
      sb.append("<body>" + body + "<body>");
      return sb.toString();
   }

   @Override
   public ByteBuffer toByteBuffer() throws UnsupportedEncodingException
   {
      if (isPing())
      {
         ByteBuffer buffer = ByteBuffer.allocateDirect(1);
         buffer.put((byte)0x0A);
         System.out.println(">>>>>>>>>>>>>>>>>>>> is ping, return buffer: " + buffer);
         buffer.rewind();
         return buffer;
      }
      StringBuffer sb = new StringBuffer();
      sb.append(command + "\n");
      int n = headers.size();
      for (int i = 0; i < n; i++)
      {
         sb.append(headers.get(i).key + ":" + headers.get(i).val + "\n");
      }
      sb.append("\n");
      if (body != null)
      {
         sb.append(body);
      }
      sb.append((char)0);
      
      String data = sb.toString();
      
      byte[] byteValue = data.getBytes("UTF-8");
      
      ByteBuffer buffer = ByteBuffer.allocateDirect(byteValue.length);
      buffer.put(byteValue);
      
      buffer.rewind();
      return buffer;
   }

   @Override
   public ByteBuffer toByteBufferWithExtra(String str) throws UnsupportedEncodingException
   {
      StringBuffer sb = new StringBuffer();
      sb.append(command + "\n");
      int n = headers.size();
      for (int i = 0; i < n; i++)
      {
         sb.append(headers.get(i).key + ":" + headers.get(i).val + "\n");
      }
      sb.append("\n");
      if (body != null)
      {
         sb.append(body);
      }
      sb.append((char)0);
      sb.append(str);
      
      String data = sb.toString();
      
      byte[] byteValue = data.getBytes("UTF-8");
      
      ByteBuffer buffer = ByteBuffer.allocateDirect(byteValue.length);
      buffer.put(byteValue);
      
      buffer.rewind();
      return buffer;
   }

   @Override
   public boolean needsReply()
   {
      if ("CONNECT".equals(command) || headerKeys.contains(HEADER_RECEIPT))
      {
         return true;
      }
      return false;
   }

   @Override
   public void setCommand(String command)
   {
      this.command = command;
   }

   @Override
   public void addHeader(String key, String val)
   {
      headers.add(new Header(key, val));
      headerKeys.add(key);
   }

   @Override
   public void setBody(String body)
   {
      this.body = body;
   }
   
   @Override
   public String getBody()
   {
      return body;
   }
   
   private class Header
   {
      public String key;
      public String val;
      
      public Header(String key, String val)
      {
         this.key = key;
         this.val = val;
      }
   }

   @Override
   public String getCommand()
   {
      return command;
   }

   @Override
   public String getHeader(String header)
   {
      if (headerKeys.contains(header))
      {
         Iterator<Header> iter = headers.iterator();
         while (iter.hasNext())
         {
            Header h = iter.next();
            if (h.key.equals(header))
            {
               return h.val;
            }
         }
      }
      return null;
   }

}
