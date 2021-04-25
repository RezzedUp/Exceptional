package com.rezzedup.util.exceptional;

@FunctionalInterface
public interface ExceptionHandler<E extends Throwable>
{
    void handle(E exception);
    
    static <E extends Throwable> void print(E exception)
    {
        exception.printStackTrace();
    }
    
    static <E extends Throwable> void rethrow(E exception)
    {
        throw new Rethrow(exception);
    }
}
