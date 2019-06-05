package cn.edu.zjicm.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import cn.edu.zjicm.Adapter.ExpandableListViewAdapter;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.Utils.ScreenUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.chat.FriendRoomActivity;
import cn.edu.zjicm.chat.InformationInActivity;
import cn.edu.zjicm.chat.MainActivity;
import cn.edu.zjicm.chat.R;
import cn.edu.zjicm.toos.FriendGroup;
import cn.edu.zjicm.toos.FriendInform;
import cn.edu.zjicm.toos.FriendResult;
import cn.edu.zjicm.toos.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class FriendFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ExpandableListViewAdapter expandableListViewAdapter;
    private ImageView imge_hunt;
    private String toId;
    private Button button_add;
    private Button button_deselect;
    private TextView dialog_text;
    private String operation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_list,container,false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.friend_group_list);
        imge_hunt = (ImageView)view.findViewById(R.id.imge_hunt);
        initListview();
        postFriend();
        initImge();
        return view;
    }
    public void initListview(){
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FriendInform inform = (FriendInform)expandableListViewAdapter.getChild(groupPosition,childPosition);
                Intent intent = new Intent(getActivity(), FriendRoomActivity.class);
                intent.putExtra("toId",inform.getId());
                getActivity().startActivity(intent);
                return false;
            }
        });
        expandableListView.setOnItemLongClickListener(onItemLongClickListener);


    }
    public void postFriend(){
        final String id = (String) SharedPreferencesUtils.getInstance().getParam("id","");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("id",id).build();
        final Request request = new Request.Builder().url(ChatUtils.imgUrl+"/friendList").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast=Toast.makeText(getActivity(),"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    final String json = response.body().string();
                    Log.i("friend",json);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           FriendResult result = new Gson().fromJson(json,FriendResult.class);
                           List<FriendGroup> friendGroups = result.getFriendList();
                           expandableListViewAdapter = new ExpandableListViewAdapter(getActivity(),friendGroups);
                           expandableListView.setAdapter(expandableListViewAdapter);
                        }
                    });
            }
        });
    }
    public void initImge(){
        imge_hunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
            }
        });
    }
    public void postAddFriend(final AlertDialog dialog){
        OkHttpClient client = new OkHttpClient();
        String id = (String)SharedPreferencesUtils.getInstance().getParam("id","");
        RequestBody requestBody = new FormBody.Builder().add("operation",operation).add("user_id",id).add("friend_id",toId).build();
        Request request = new Request.Builder().url(ChatUtils.imgUrl+ "/Friend").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity()== null){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast=Toast.makeText(getActivity(),"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                Log.i("add",json);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = new Gson().fromJson(json,Result.class);
                        if (result.getStatus().equals("1")){
                            Toast toast=Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            postFriend();
                            dialog.dismiss();

                        }else {
                            Toast toast=Toast.makeText(getActivity(),"操作失败请重新输入",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                        }
                    }
                });
            }
        });
    }
    public void postDeleteFriend(){
        OkHttpClient client = new OkHttpClient();
        String id = (String)SharedPreferencesUtils.getInstance().getParam("id","");
        RequestBody requestBody = new FormBody.Builder().add("operation",operation).add("user_id",id).add("friend_id",toId).build();
        Request request = new Request.Builder().url(ChatUtils.imgUrl+ "/Friend").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity()== null){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast=Toast.makeText(getActivity(),"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = new Gson().fromJson(json,Result.class);
                        if (result.getStatus().equals("1")){
                            Toast toast=Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            postFriend();
                        }else {
                            Toast toast=Toast.makeText(getActivity(),"操作失败请重新输入",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                        }
                    }
                });
            }
        });
    }
    public void addDialog(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog_text = (TextView)view.findViewById(R.id.dialog_text);
        button_add = (Button)view.findViewById(R.id.button_add);
        button_deselect = (Button)view.findViewById(R.id.button_deselect);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toId = dialog_text.getText().toString();
                operation = "add";
                postAddFriend(dialog);

            }
        });
        button_deselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(900,500);

    }
    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final long packedPosition = expandableListView.getExpandableListPosition(position);
            final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
            final int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
            //长按的是group的时候，childPosition = -1
            if (childPosition != -1) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("是否删除该好友");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendInform inform = (FriendInform)expandableListViewAdapter.getChild(groupPosition,childPosition);
                        toId = inform.getId();
                        operation = "delete";
                        postDeleteFriend();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            return true;
        }
    };
    @Override
    public void onResume() {
        super.onResume();
    }
}
