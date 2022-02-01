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
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface CheckedToLongFunction<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedToLongFunction<T, E>, E>, ToLongFunction<T>
{
    static <T, E extends Throwable> CheckedToLongFunction<T, E> of(CheckedToLongFunction<T, E> function)
    {
        return function;
    }
    
    static <T, E extends Throwable> CheckedToLongFunction<T, E> of(Catcher<Throwable> catcher, CheckedToLongFunction<T, E> function)
    {
        return function.catcher(catcher);
    }
    
    long applyAsLongOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default long applyAsLong(T t)
    {
        try { return applyAsLongOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedToLongFunction<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedToLongFunction<T, E>
        {
            CheckedToLongFunction<T, E> origin() { return CheckedToLongFunction.this; }
            
            @Override
            public long applyAsLongOrThrow(T t) throws E { return origin().applyAsLongOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
