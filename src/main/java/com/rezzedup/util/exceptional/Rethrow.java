package com.rezzedup.util.exceptional;

import java.util.Objects;

public class Rethrow extends RuntimeException
{
    public Rethrow(Throwable cause)
    {
        super(Objects.requireNonNull(cause, "cause"));
    }
    
    @Override // overridden to assert non-null return value
    public synchronized Throwable getCause()
    {
        return super.getCause();
    }
}
