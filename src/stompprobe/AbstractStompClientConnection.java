package stompprobe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractStompClientConnection implements StompClientConnection
{
   protected static final String CONNECT_COMMAND = "CONNECT";
   protected static final String CONNECTED_COMMAND = "CONNECTED";
   protected static final String DISCONNECT_COMMAND = "DISCONNECT";
   
   protected static final String LOGIN_HEADER = "login";
   protected static final String PASSCODE_HEADER = "passcode";
   
   //ext
   protected static final String CLIENT_ID_HEADER = "client-id";
   
   
   protected String version;
   protected String host;
   protected int port;
   protected String username;
   protected String passcode;
   protected StompFrameFactory factory;
   protected SocketChannel socketChannel;
   protected ByteBuffer readBuffer;
   
   protected List<Byte> receiveList;
   
   protected BlockingQueue<ClientStompFrame> frameQueue = new LinkedBlockingQueue<ClientStompFrame>();
   
   protected boolean connected = false;

   public AbstractStompClientConnection(String version, String host, int port) throws IOException
   {
      this.version = version;
      this.host = host;
      this.port = port;
      this.factory = StompFrameFactoryFactory.getFactory(version);
      
      initSocket();
   }

   private void initSocket() throws IOException
   {
      socketChannel = SocketChannel.open();
      socketChannel.configureBlocking(true);
      InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
      socketChannel.connect(remoteAddr);
      
      startReaderThread();
   }
   
   private void startReaderThread()
   {
      readBuffer = ByteBuffer.allocateDirect(10240);
      receiveList = new ArrayList<Byte>(10240);
      
      new ReaderThread().start();
   }
   
   public ClientStompFrame sendFrame(ClientStompFrame frame) throws IOException, InterruptedException
   {
      ClientStompFrame response = null;
      ByteBuffer buffer = frame.toByteBuffer();
      System.out.println("_____frame buffer remaining: " + buffer.remaining());
      while (buffer.remaining() > 0)
      {
         System.out.println("remaining " + buffer.remaining());
         System.out.println("____sending to socket: " + buffer);
         socketChannel.write(buffer);
      }
      System.out.println("______buffer snet.");
      
      //now response
      if (frame.needsReply())
      {
         response = receiveFrame();
         
         //filter out server ping
         while (response != null)
         {
            if (response.getCommand().equals("STOMP"))
            {
               response = receiveFrame();
            }
            else
            {
               break;
            }
         }
      }
      return response;
   }

   public ClientStompFrame receiveFrame() throws InterruptedException
   {
      return frameQueue.poll(10, TimeUnit.SECONDS);
   }

   public ClientStompFrame receiveFrame(long timeout) throws InterruptedException
   {
      return frameQueue.poll(timeout, TimeUnit.MILLISECONDS);
   }
   
   //put bytes to byte array.
   private void receiveBytes(int n) throws UnsupportedEncodingException
   {
      readBuffer.rewind();
      for (int i = 0; i < n; i++)
      {
         byte b = readBuffer.get();
         if (b == 0)
         {
            //a new frame got.
            int sz = receiveList.size();
            if (sz > 0)
            {
               byte[] frameBytes = new byte[sz];
               for (int j = 0; j < sz; j++)
               {
                  frameBytes[j] = receiveList.get(j);
               }
               ClientStompFrame frame = factory.createFrame(new String(frameBytes, "UTF-8"));
               
               if (validateFrame(frame))
               {
                 frameQueue.offer(frame);
                 receiveList.clear();
               }
               else
               {
                  receiveList.add(b);
               }
            }
         }
         else
         {
            receiveList.add(b);
         }
      }
      //clear readbuffer
      readBuffer.rewind();
   }
   
   private boolean validateFrame(ClientStompFrame f) throws UnsupportedEncodingException
   {
      String h = f.getHeader("content-length");
      if (h != null)
      {
         int len = Integer.valueOf(h);
         if (f.getBody().getBytes("UTF-8").length < len)
         {
            return false;
         }
      }
      return true;
   }
   
   protected void close() throws IOException
   {
      socketChannel.close();
   }
   
   private class ReaderThread extends Thread
   {
      public void run()
      {
         try
         {
            int n = socketChannel.read(readBuffer);
            
            while (n >= 0)
            {
               if (n > 0)
               {
                  receiveBytes(n);
               }
               n = socketChannel.read(readBuffer);
            }
            //peer closed
            close();
         
         } 
         catch (IOException e)
         {
            try
            {
               close();
            } 
            catch (IOException e1)
            {
               //ignore
            }
         }
      }
   }

   public void connect() throws Exception
   {
      connect(null, null);
   }
   
   public void destroy()
   {
      try
      {
         close();
      }
      catch (IOException e)
      {
      }
      finally
      {
         this.connected = false;
      }
   }
   
   public void connect(String username, String password) throws Exception
   {
      throw new RuntimeException("connect method not implemented!");
   }
   
   public boolean isConnected()
   {
      return connected;
   }
   
   public String getVersion()
   {
      return version;
   }
   
   public int getFrameQueueSize()
   {
      return this.frameQueue.size();
   }

}
