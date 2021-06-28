package com.rezzedup.util.exceptional.checked;

/**
 * @see java.util.function.Supplier
 */
public interface CheckedSupplier<T, E extends Throwable>
{
    T get() throws E;
}
