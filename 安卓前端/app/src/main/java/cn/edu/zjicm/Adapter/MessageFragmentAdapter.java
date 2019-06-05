package cn.edu.zjicm.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zjicm.chat.ChatRoomActivity;
import cn.edu.zjicm.chat.R;
import cn.edu.zjicm.view.CircleImageView;

public class MessageFragmentAdapter extends RecyclerView.Adapter<MessageFragmentAdapter.ViewHolder>{
    private List<String> list;

    public MessageFragmentAdapter(){
        this.list = new ArrayList<>();
        list.add("公共聊天室");
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView chatname;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            chatname = (TextView)itemView.findViewById(R.id.chatroom_tite);

        }
    }
    private Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        context = viewGroup.getContext();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.chatname.setText(list.get(i));
        CircleImageView circleImageView = (CircleImageView)viewHolder.itemView.findViewById(R.id.circleImageView);
        Glide.with(context).load(R.drawable.ic_default_minerva).into(circleImageView);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
