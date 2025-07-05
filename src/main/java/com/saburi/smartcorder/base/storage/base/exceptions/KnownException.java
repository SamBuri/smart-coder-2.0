package com.saburi.smartcorder.base.storage.base.exceptions;

public class KnownException extends Exception{
   private  int code;
    private String message;

    public KnownException(String message) {
        super(message);
    }

    public KnownException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
