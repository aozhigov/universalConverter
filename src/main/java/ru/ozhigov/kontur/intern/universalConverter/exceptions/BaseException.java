package ru.ozhigov.kontur.intern.universalConverter.exceptions;

public class BaseException extends Exception {
    private final int code;

    public BaseException(String message, int code){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
