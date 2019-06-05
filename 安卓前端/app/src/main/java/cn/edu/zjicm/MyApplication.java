package cn.edu.zjicm;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.litepal.LitePal;

import java.net.URISyntaxException;

import cn.edu.zjicm.Utils.ChatUtils;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MyApplication extends Application {
    private static Context context;
    private Socket msocket;
    {
        try {
            IO.Options opts = new IO.Options();

            msocket = IO.socket(ChatUtils.URL,opts);
        } catch (URISyntaxException e) {

            Log.i("e", String.valueOf(e));
        }
    }

    public Socket getMsocket() {
        return msocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
