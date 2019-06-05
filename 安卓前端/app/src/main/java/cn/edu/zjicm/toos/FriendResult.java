package cn.edu.zjicm.toos;

import java.util.List;

public class FriendResult {
   private List<FriendGroup> friendList;
   public FriendResult(List<FriendGroup> friendList){
       this.friendList = friendList;

   }
    public List<FriendGroup> getFriendList() {
        return friendList;
    }
}
