package cn.edu.zjicm.toos;

public class Result  {
    private String status;
    private String token;
    private String name;
    private String url;
    private String id;
    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
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

    public Result(String token, String status, String name, String url, String id){
        this.token = token;
        this.status = status;
        this.name = name;
        this.url = url;
        this.id = id;
    }
    public Result(String status){
        this.status = status;
    }
}
