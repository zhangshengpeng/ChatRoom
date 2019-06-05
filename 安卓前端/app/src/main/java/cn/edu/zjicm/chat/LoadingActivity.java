package cn.edu.zjicm.chat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.zjicm.Permission.PermissionUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;

public class LoadingActivity extends FragmentActivity {
    private Timer timer;
    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(LoadingActivity.this,InformationInActivity.class);
                    startActivity(intent);
                    timer.cancel();
                    finish();
                    break;
                case 2:
                    Intent intent1 = new Intent(LoadingActivity.this,MainActivity.class);
                    startActivity(intent1);
                    timer.cancel();
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_loading);
        timer = new Timer(true);
        timer.schedule(task,1500);

    }
    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            String id = (String) SharedPreferencesUtils.getInstance().getParam("id","");
            if (id.equals("")){
                message.what = 1;
            }else {
               message.what = 2;
            }
            handler.sendMessage(message);
        }
    };
}
