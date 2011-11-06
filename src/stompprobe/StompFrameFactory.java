package stompprobe;

public interface StompFrameFactory
{

   ClientStompFrame createFrame(String data);

   ClientStompFrame newFrame(String command);

}
