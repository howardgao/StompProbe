package stompprobe;

import java.io.IOException;

public class StompClientConnectionV10 extends AbstractStompClientConnection
{
   public StompClientConnectionV10(String host, int port) throws IOException
   {
      super("1.0", host, port);
   }

   public void connect(String username, String passcode) throws IOException, InterruptedException
   {
      ClientStompFrame frame = factory.newFrame(CONNECT_COMMAND);
      frame.addHeader(LOGIN_HEADER, username);
      frame.addHeader(PASSCODE_HEADER, passcode);
      
      ClientStompFrame response = this.sendFrame(frame);
      
      if (response.getCommand().equals(CONNECTED_COMMAND))
      {
         connected = true;
      }
      else
      {
         System.out.println("Connection failed with: " + response);
         connected = false;
      }
   }

   public void connect(String username, String passcode, String clientID) throws IOException, InterruptedException
   {
      ClientStompFrame frame = factory.newFrame(CONNECT_COMMAND);
      frame.addHeader(LOGIN_HEADER, username);
      frame.addHeader(PASSCODE_HEADER, passcode);
      frame.addHeader(CLIENT_ID_HEADER, clientID);
      
      ClientStompFrame response = this.sendFrame(frame);
      
      if (response.getCommand().equals(CONNECTED_COMMAND))
      {
         connected = true;
      }
      else
      {
         System.out.println("Connection failed with: " + response);
         connected = false;
      }
   }

   @Override
   public void disconnect() throws IOException, InterruptedException
   {
      ClientStompFrame frame = factory.newFrame(DISCONNECT_COMMAND);
      this.sendFrame(frame);
      
      close();
      
      connected = false;
   }

   @Override
   public ClientStompFrame createFrame(
         String command)
   {
      return new ClientStompFrameV10(command);
   }

   @Override
   public void startPinger(long interval)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void stopPinger()
   {
      // TODO Auto-generated method stub
      
   }
}
