package com.rezzedup.util.exceptional.unchecked;

public interface Unchecked<E extends Throwable>
{
    Throwable getCause();
    
    @SuppressWarnings("unchecked")
    default E checked()
    {
        return (E) getCause();
    }
}
