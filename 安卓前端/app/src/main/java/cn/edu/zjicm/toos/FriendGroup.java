package cn.edu.zjicm.toos;

import android.net.http.SslCertificate;

import java.util.List;

public class FriendGroup {
    private String groupName = null;
    private List<FriendInform> friends = null;
    public FriendGroup(String groupName,List<FriendInform> informs){
        this.groupName = groupName;
        this.friends = informs;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<FriendInform> getFriends() {
        return friends;
    }
}
