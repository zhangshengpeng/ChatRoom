package cn.edu.zjicm.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.litepal.LitePal;

import cn.edu.zjicm.Adapter.MessageFragmentAdapter;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.chat.InformationInActivity;
import cn.edu.zjicm.chat.R;

public class MessageFragment extends Fragment {
    private RecyclerView recyclerView;
    private MessageFragmentAdapter ms;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_list,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.message_chatmessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ms = new MessageFragmentAdapter();
        recyclerView.setAdapter(ms);
        return view;
    }
}
