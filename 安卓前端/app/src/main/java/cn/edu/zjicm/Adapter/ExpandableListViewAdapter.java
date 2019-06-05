package cn.edu.zjicm.Adapter;

import android.content.Context;
import android.graphics.ColorSpace;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.security.CryptoPrimitive;
import java.util.List;

import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.chat.R;
import cn.edu.zjicm.toos.FriendGroup;
import cn.edu.zjicm.view.CircleImageView;


public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<FriendGroup> groups;
    private LayoutInflater mInflater;
    private RequestOptions options;
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            notifyDataSetChanged();//更新数据
//            super.handleMessage(msg);
//        }
//    };
    public ExpandableListViewAdapter(Context context,List<FriendGroup> results){
        this.context = context;
        this.groups = results;
        mInflater = LayoutInflater.from(context);

    }
//    public void refresh(ExpandableListView expandableListView, int groupPosition){
//        handler.sendMessage(new Message());
//        //必须重新伸缩之后才能更新数据
//        expandableListView.collapseGroup(groupPosition);
//        expandableListView.expandGroup(groupPosition);
//    }
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getFriends().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getFriends().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup group;
        options = new RequestOptions()
                .fallback(R.drawable.ic_default_minerva)
                .error(R.drawable.ic_default_minerva);
        if (convertView == null){
            group = new ViewHolderGroup();
            convertView =  mInflater.inflate(R.layout.friend_item,parent,false);
            group.txt_group_name = (TextView)convertView.findViewById(R.id.text_group_name);
            convertView.setTag(group);
        }else {
            group = (ViewHolderGroup) convertView.getTag();
        }
        group.txt_group_name.setText(groups.get(groupPosition).getGroupName());
        return convertView;
        }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderFriend friend;
        if (convertView == null){
            friend = new ViewHolderFriend();
            convertView =  mInflater.inflate(R.layout.friend_child_item,parent,false);
            friend.circleImageView = (CircleImageView) convertView.findViewById(R.id.circleImageView);
            friend.friend_name = (TextView)convertView.findViewById(R.id.text_friend_name);
            convertView.setTag(friend);
        }else {
            friend = (ViewHolderFriend) convertView.getTag();
        }
        friend.friend_name.setText(groups.get(groupPosition).getFriends().get(childPosition).getName());
        Glide.with(context).load(ChatUtils.imgUrl+groups.get(groupPosition).getFriends().get(childPosition).getUrl()).apply(options).into(friend.circleImageView);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    private static class ViewHolderGroup{
        public TextView txt_group_name;

    }
    private static class ViewHolderFriend{
        public TextView friend_name;
        public CircleImageView circleImageView;
    }
}
