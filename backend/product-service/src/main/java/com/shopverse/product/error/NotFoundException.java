package com.shopverse.product.error;

// Parent of every "this thing doesn't exist" error. The global handler turns ALL of them into 404.
public class NotFoundException extends RuntimeException{
    protected NotFoundException(String message){
        super(message);
    }
}
