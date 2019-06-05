package cn.edu.zjicm.toos;


import org.litepal.crud.LitePalSupport;

public class MessageHistory extends LitePalSupport {

    private String name;
    private String message;
    private String url;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
