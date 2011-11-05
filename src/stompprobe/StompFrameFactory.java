package org.hornetq.test.stomp.client;

public interface StompFrameFactory
{

   ClientStompFrame createFrame(String data);

   ClientStompFrame newFrame(String command);

}
