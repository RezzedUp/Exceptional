/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@code Supplier} that can throw checked exceptions.
 *
 * @param <T>   return type
 * @param <E>   exception type
 *
 * @see Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> extends Catcher.Swap<Throwable, CheckedSupplier<T, E>>, Supplier<T>
{
    /**
     * Gets a result.
     *
     * @return a result
     * @throws E a checked exception
     */
    T getOrThrow() throws E;
    
    @Override
    default @NullOr T get()
    {
        try { return getOrThrow(); }
        catch (Throwable e)
        {
            catcher().handleSafely(e);
            return null;
        }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedSupplier<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedSupplier<T, E>
        {
            CheckedSupplier<T, E> origin() { return CheckedSupplier.this; }
            
            @Override
            public T getOrThrow() throws E { return origin().getOrThrow(); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
