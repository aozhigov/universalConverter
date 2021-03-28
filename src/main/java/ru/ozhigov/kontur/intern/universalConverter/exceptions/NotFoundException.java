package ru.ozhigov.kontur.intern.universalConverter.exceptions;

public class NotFoundException extends BaseException{
    public NotFoundException(String message){
        super("Cannot convert to " + message, 404);
    }
}
