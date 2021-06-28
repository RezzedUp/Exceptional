package com.rezzedup.util.exceptional.checked;

/**
 * @param <E>
 *
 * @see Runnable
 */
public interface CheckedRunnable<E extends Throwable>
{
    void run() throws E;
}
