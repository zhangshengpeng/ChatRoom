package cn.edu.zjicm.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zjicm.Adapter.ChatRoomAdapter;
import cn.edu.zjicm.MyApplication;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.toos.Message;
import cn.edu.zjicm.toos.MessageHistory;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendRoomActivity extends Activity {
    private Socket mSocket;
    private EditText chatroom;
    private ImageView send_message;
    private List<Message> list = new ArrayList<>();
    private ChatRoomAdapter chatRoomAdapter;
    private RecyclerView recyclerView;
    private String toID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.chatroom_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendRoomActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        toID = intent.getStringExtra("toId");

        init();
    }
    public void init(){
        chatroom = (EditText)findViewById(R.id.chatroomedit_text);
        chatroom.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptSend();
                chatroom.setText("");
                return true;
            }
        });
        send_message = (ImageView)findViewById(R.id.send_message);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
                chatroom.setText("");
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendRoomActivity.this));

//        ChatUtils.mSocket.on(Socket.EVENT_CONNECT,onConnect);
        ChatUtils.mSocket.on("sayTo", onNewMessage);


    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            FriendRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final String json = args[0].toString();
                    Log.i("js",json);
                    Message message = new Gson().fromJson(json,Message.class);
                    if (message.getStates().equals("1")){

                        if (message.getName().equals(SharedPreferencesUtils.getInstance().getParam("name",""))){
                            message.setType(0);

                        }else if (message.getName().equals(SharedPreferencesUtils.getInstance().getParam("name",""))==false){
                            message.setType(1);
                        }
                        list.add(message);
                        chatRoomAdapter = new ChatRoomAdapter(FriendRoomActivity.this,list);
                        chatRoomAdapter.notifyItemInserted(list.size()-1);
                        recyclerView.setAdapter(chatRoomAdapter);
                        recyclerView.scrollToPosition(list.size()-1);
                    }else {
                        Toast toast = Toast.makeText(FriendRoomActivity.this,"该用户不在线",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
            });
        }
    };


    private void attemptSend() {
        if (TextUtils.isEmpty(chatroom.getText().toString())) {
            chatroom.requestFocus();
            return;
        }
        String id = (String)SharedPreferencesUtils.getInstance().getParam("id","");
        String name = (String) SharedPreferencesUtils.getInstance().getParam("name","");
        String message = chatroom.getText().toString();
        String url = "http://101.132.116.167:666"+SharedPreferencesUtils.getInstance().getParam("url","");
        String json = new Gson().toJson(new Message(name,message,url,id,toID));
        ChatUtils.mSocket.emit("sayTo",json );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

