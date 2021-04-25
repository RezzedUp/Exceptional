package com.rezzedup.util.exceptional.unchecked;

import com.rezzedup.util.exceptional.Rethrow;

import java.io.IOException;

public class UncheckedIOException extends Rethrow implements Unchecked<IOException>
{
    public UncheckedIOException(IOException cause)
    {
        super(cause);
    }
}
