package dev.esanchez.timely.backend.core.globalException.customGlobalException;


public class NotFoundException extends RuntimeException {

    public NotFoundException() {}

    public NotFoundException(String msg , Long Id) {
        super( msg + Id);
    }
    public NotFoundException(String msg , String field) {
        super( msg + field);
    }
    public NotFoundException(String entity){
        super(entity+" not found.");
    }
}