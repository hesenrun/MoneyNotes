package com.bqmz001.moneynotes.entity;

public class EventBean {
    private int msgId;
    private String msg;
    private String parameter;

    public EventBean(){
    }

    public EventBean(int msgId,String msg,String parameter){
        this.msg=msg;
        this.msgId=msgId;
        this.parameter=parameter;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
