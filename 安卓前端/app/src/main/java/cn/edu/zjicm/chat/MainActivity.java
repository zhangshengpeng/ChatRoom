package cn.edu.zjicm.chat;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zjicm.Adapter.FragmentAdapter;
import cn.edu.zjicm.MyApplication;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.fragment.FriendFragment;
import cn.edu.zjicm.fragment.MessageFragment;
import cn.edu.zjicm.fragment.PersionFragment;
import cn.edu.zjicm.view.NoScrollViewPager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationBar bottomNavigationBar;
    private NoScrollViewPager viewPager;
    private PersionFragment persionFragment;
    private FriendFragment friendFragment;
    private MessageFragment informationFragment;
    private List<Fragment> fragments;
    private Boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatUtils.db = LitePal.getDatabase();
        MyApplication application = (MyApplication) MainActivity.this.getApplication();
        ChatUtils.mSocket = application.getMsocket();
        ChatUtils.mSocket.connect();
        ChatUtils.mSocket.on(Socket.EVENT_CONNECT,onConnect);
        ChatUtils.mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        initViewPage();
        initBottom();
    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if(!isConnected) {
                        String s = (String)SharedPreferencesUtils.getInstance().getParam("id","");
                        ChatUtils.mSocket.emit("online",s);
                        isConnected = true;
                    }
                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    isConnected = false;
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public void initViewPage(){
        viewPager = (NoScrollViewPager)findViewById(R.id.no_scrollViewpage);
        viewPager.setNoScroll(true);
        persionFragment = new PersionFragment();
        friendFragment = new FriendFragment();
        informationFragment = new MessageFragment();
        fragments = new ArrayList<>();
        fragments.add(informationFragment);
        fragments.add(friendFragment);
        fragments.add(persionFragment);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                bottomNavigationBar.selectTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    public void initBottom(){
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_nav_bar);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.na_infor,"消息")
                .setInactiveIconResource(R.drawable.na_infor1)
                .setActiveColorResource(R.color.na_bar))
                .addItem(new BottomNavigationItem(R.drawable.na_friend,"好友")
                        .setInactiveIconResource(R.drawable.na_friend1)
                        .setActiveColorResource(R.color.na_bar))
                .addItem(new BottomNavigationItem(R.drawable.na_person,"个人")
                        .setInactiveIconResource(R.drawable.na_person1)
                        .setActiveColorResource(R.color.na_bar))
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatUtils.mSocket.disconnect();
        ChatUtils.mSocket.off(Socket.EVENT_CONNECT,onConnect);
        ChatUtils.mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
