/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;

import java.util.Objects;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface CheckedLongSupplier<E extends Throwable>
    extends Catcher.Swap<CheckedLongSupplier<E>, Throwable>, LongSupplier
{
    static <E extends Throwable> CheckedLongSupplier<E> of(CheckedLongSupplier<E> supplier)
    {
        return supplier;
    }
    
    static <E extends Throwable> CheckedLongSupplier<E> of(Catcher<Throwable> catcher, CheckedLongSupplier<E> supplier)
    {
        return supplier.catcher(catcher);
    }
    
    long getAsLongOrThrow() throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default long getAsLong()
    {
        try { return getAsLongOrThrow(); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0L;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongSupplier<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongSupplier<E>
        {
            CheckedLongSupplier<E> origin() { return CheckedLongSupplier.this; }
            
            @Override
            public long getAsLongOrThrow() throws E { return origin().getAsLongOrThrow(); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
