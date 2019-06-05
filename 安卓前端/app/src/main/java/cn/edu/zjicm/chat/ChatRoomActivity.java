package cn.edu.zjicm.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import cn.edu.zjicm.Adapter.ChatRoomAdapter;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.toos.Message;
import cn.edu.zjicm.MyApplication;
import cn.edu.zjicm.toos.MessageHistory;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatRoomActivity extends Activity {
    private Socket mSocket;
    private EditText chatroom;
    private ImageView send_message;
    private List<Message> list = new ArrayList<>();
    private ChatRoomAdapter chatRoomAdapter;
    private RecyclerView recyclerView;
    private String toid = null;


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
                ChatRoomActivity.this.finish();
            }
        });
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
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));
        List<MessageHistory> mh = LitePal.findAll(MessageHistory.class);
        if (mh.size()>0)
        {
            for (MessageHistory messageHistory:mh){
                Message message = new Message(messageHistory.getName(),messageHistory.getMessage(),messageHistory.getUrl());
                if (messageHistory.getName().equals(SharedPreferencesUtils.getInstance().getParam("name","")))
                {
                    message.setType(0);
                }else{
                    message.setType(1);
                }
                list.add(message);
                chatRoomAdapter = new ChatRoomAdapter(MyApplication.getContext(),list);
                recyclerView.setAdapter(chatRoomAdapter);
            }
        }


//        ChatUtils.mSocket.on(Socket.EVENT_CONNECT,onConnect);
        ChatUtils.mSocket.on("message", onNewMessage);


    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("sss", args[0].toString());
                    final String json = args[0].toString();
                    Message message = new Gson().fromJson(json,Message.class);
                    MessageHistory history = new MessageHistory();
                    history.setName(message.getName());
                    history.setMessage(message.getMessage());
                    history.setUrl(message.getUrl());
                    if (message.getName().equals(SharedPreferencesUtils.getInstance().getParam("name",""))){
                        message.setType(0);
                        history.save();
                    }else if (message.getName().equals(SharedPreferencesUtils.getInstance().getParam("name",""))==false){
                        message.setType(1);
                        history.save();
                    }
                    list.add(message);
                    chatRoomAdapter = new ChatRoomAdapter(ChatRoomActivity.this,list);
                    chatRoomAdapter.notifyItemInserted(list.size()-1);
                    recyclerView.setAdapter(chatRoomAdapter);
                    recyclerView.scrollToPosition(list.size()-1);


                }
            });
        }
    };


    private void attemptSend() {
        if (TextUtils.isEmpty(chatroom.getText().toString())) {
            chatroom.requestFocus();
            return;
        }
        String name = (String) SharedPreferencesUtils.getInstance().getParam("name","");
        String message = chatroom.getText().toString();
        String url = "http://101.132.116.167:666"+SharedPreferencesUtils.getInstance().getParam("url","");
        String json = new Gson().toJson(new Message(name, message,url));
        ChatUtils.mSocket.emit("message", json);
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
