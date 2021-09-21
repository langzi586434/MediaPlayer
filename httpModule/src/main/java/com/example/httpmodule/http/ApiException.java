package com.example.httpmodule.http;

public class ApiException extends Exception {
    private String msg;
    private int code;
    public ApiException(Throwable throwable, int code){
        super(throwable);
        this.code = code;
    }

    public ApiException(String msg, int code){
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}