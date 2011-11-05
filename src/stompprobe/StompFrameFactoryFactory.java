package org.hornetq.test.stomp.client;

public class StompFrameFactoryFactory
{
   public static StompFrameFactory getFactory(String version)
   {
      if ("1.0".equals(version))
      {
         return new StompFrameFactoryV10();
      }
      
      if ("1.1".equals(version))
      {
         return new StompFrameFactoryV11();
      }
      
      return null;
   }

}
