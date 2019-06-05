package cn.edu.zjicm.toos;

public class FriendInform {
   private String name;
   private String url;
   private String id;
   public FriendInform(String name,String url,String id){
       this.name = name;
       this.url = url;
       this.id = id;
   }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}
