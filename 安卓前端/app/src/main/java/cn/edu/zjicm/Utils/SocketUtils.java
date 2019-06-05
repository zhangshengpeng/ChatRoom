package cn.edu.zjicm.Utils;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketUtils {
    private Socket mSocket;
    public static SocketUtils socketUtils =null;
    public static SocketUtils getInstance(){
        if(socketUtils==null) {
            socketUtils=new SocketUtils();
        }
        return socketUtils;}
        public SocketUtils() { }/*** 初始化 Socket*/
        public void init() {
            if(mSocket!=null&&mSocket.connected())
                return;
            try{
                mSocket= IO.socket(ChatUtils.URL);
                }catch(Exception e) {
                e.printStackTrace();
            }
        }

    public Socket getmSocket() {
        return mSocket;
    }
}
