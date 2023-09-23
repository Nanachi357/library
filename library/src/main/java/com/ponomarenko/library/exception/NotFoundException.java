package com.ponomarenko.library.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String name, Long id){
        super("Could not find " + name + " " + id);
    }
}
