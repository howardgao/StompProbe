package stompprobe;

import java.io.IOException;


public interface StompClientConnection
{
   ClientStompFrame sendFrame(ClientStompFrame frame) throws IOException, InterruptedException;
   
   ClientStompFrame receiveFrame() throws InterruptedException;

   ClientStompFrame receiveFrame(long timeout) throws InterruptedException;;

   void connect() throws Exception;

   void disconnect() throws IOException, InterruptedException;

   void connect(String defUser, String defPass) throws Exception;

   void connect(String defUser, String defPass, String clientId) throws Exception;

   boolean isConnected();

   String getVersion();

   ClientStompFrame createFrame(String command);

   //number of frames at the queue
   int getFrameQueueSize();

   void startPinger(long interval);

   void stopPinger();

   void destroy();
}

