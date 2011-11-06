package stompprobe;

public class ClientStompFrameV11 extends AbstractClientStompFrame
{
   boolean forceOneway = false;
   boolean isPing = false;
   
   public ClientStompFrameV11(String command)
   {
      super(command);
   }
   
   public void setForceOneway()
   {
      forceOneway = true;
   }

   @Override
   public boolean needsReply()
   {
      if (forceOneway) return false;
      
      if ("CONNECT".equals(command) || "STOMP".equals(command) || headerKeys.contains(HEADER_RECEIPT))
      {
         return true;
      }
      return false;
   }

   public void setPing(boolean b)
   {
      isPing = b;
   }
   
   public boolean isPing()
   {
      return isPing;
   }
}
