package cn.edu.zjicm.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

import cn.edu.zjicm.MyApplication;
import cn.edu.zjicm.chat.R;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.toos.Message;
import cn.edu.zjicm.view.CircleImageView;
import static android.content.Context.MODE_PRIVATE;
public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int Right = 0;
    private static final int Left = 1;
    private Context context;
    private List<Message> list;
    public ChatRoomAdapter(Context context,List<Message> list){
        this.context = context;
        this.list = list;
    }

    public void notifyDataSetChanged(int i) {
    }

    static class RightViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView message;
        private View rightView;
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rightView = itemView;
            mName = (TextView)itemView.findViewById(R.id.text_name);
            message = (TextView)itemView.findViewById(R.id.text_Msg);
        }
    }
    static class LeftViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView message;
        private View leftView;
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            this.leftView = itemView;
            mName = (TextView)itemView.findViewById(R.id.text_name);
            message = (TextView)itemView.findViewById(R.id.text_Msg);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder holder = null;
        if (i==Right){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chartrom_left,null);
            holder = new RightViewHolder(view);
            return holder;
        }else if (i==Left){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chartrommright,null);
            holder = new LeftViewHolder(view);
            return holder;
        }
        else {
            return null;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        String url = list.get(i).getUrl();
        RequestOptions options = new RequestOptions()
                .fallback(R.drawable.ic_default_minerva)
                .error(R.drawable.ic_default_minerva)
                .signature(new ObjectKey(ChatUtils.imgUrlKey));
        if (viewHolder instanceof RightViewHolder){
            ((RightViewHolder) viewHolder).mName.setText(list.get(i).getName());
            ((RightViewHolder) viewHolder).message.setText(list.get(i).getMessage());
            CircleImageView circleImageView = (CircleImageView)((RightViewHolder) viewHolder).rightView.findViewById(R.id.circleImageView);
            Glide.with(MyApplication.getContext()).load(url).apply(options).into(circleImageView);
        }else if (viewHolder instanceof LeftViewHolder){
            ((LeftViewHolder) viewHolder).mName.setText(list.get(i).getName());
            ((LeftViewHolder) viewHolder).message.setText(list.get(i).getMessage());
            CircleImageView circleImageView = (CircleImageView)((LeftViewHolder) viewHolder).leftView.findViewById(R.id.circleImageView);
            Glide.with(MyApplication.getContext()).load(url).apply(options).into(circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (list.get(position).getType()==0){
            return Right;
        }else if (list.get(position).getType()==1){
            return Left;
        }
        return super.getItemViewType(position);
    }
}
