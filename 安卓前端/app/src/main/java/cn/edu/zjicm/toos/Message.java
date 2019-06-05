package cn.edu.zjicm.toos;

public class Message {
    private String fromId;
    private String toId;
    private String name;
    private String message;
    private String url;
    private String states;
    private int type;
    public Message() {}
    public Message(String id,String message,String name,String toId ,String url,String states) {
        this.name = name;
        this.message = message;
        this.url = url;
        this.fromId = id;
        this.toId = toId;
        this.states = states;
    }
    public Message(String name,String message,String url,String id,String toId) {
        this.name = name;
        this.message = message;
        this.url = url;
        this.fromId = id;
        this.toId = toId;
    }
    public Message(String name, String message,String url) {
        this.name = name;
        this.message = message;
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToId() {
        return toId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getStates() {
        return states;
    }
}
