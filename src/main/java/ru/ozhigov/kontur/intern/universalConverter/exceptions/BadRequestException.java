package ru.ozhigov.kontur.intern.universalConverter.exceptions;

public class BadRequestException extends BaseException{
    public BadRequestException(String message){
        super(message + "value is missing in CSV file", 400);
    }
}