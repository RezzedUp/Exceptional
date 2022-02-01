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
import java.util.function.ToLongBiFunction;

@FunctionalInterface
public interface CheckedToLongBiFunction<T, U, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedToLongBiFunction<T, U, E>, E>, ToLongBiFunction<T, U>
{
    static <T, U, E extends Throwable> CheckedToLongBiFunction<T, U, E> of(CheckedToLongBiFunction<T, U, E> biFunction)
    {
        return biFunction;
    }
    
    static <T, U, E extends Throwable> CheckedToLongBiFunction<T, U, E> of(Catcher<Throwable> catcher, CheckedToLongBiFunction<T, U, E> biFunction)
    {
        return biFunction.catcher(catcher);
    }
    
    long applyAsLongOrThrow(T t, U u) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default long applyAsLong(T t, U u)
    {
        try { return applyAsLongOrThrow(t, u); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedToLongBiFunction<T, U, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _U, _E> implements CheckedToLongBiFunction<T, U, E>
        {
            CheckedToLongBiFunction<T, U, E> origin() { return CheckedToLongBiFunction.this; }
            
            @Override
            public long applyAsLongOrThrow(T t, U u) throws E { return origin().applyAsLongOrThrow(t, u); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, U, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
