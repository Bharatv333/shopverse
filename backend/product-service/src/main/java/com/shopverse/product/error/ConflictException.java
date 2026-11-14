package com.shopverse.product.error;

// Parent of every "the request is fine, but it clashes with the current data" error -> 409.
public abstract class ConflictException extends RuntimeException{
    protected ConflictException(String message){
        super(message);
    }
}
