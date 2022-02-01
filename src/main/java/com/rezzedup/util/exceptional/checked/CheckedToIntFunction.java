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
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface CheckedToIntFunction<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedToIntFunction<T, E>, E>, ToIntFunction<T>
{
    static <T, E extends Throwable> CheckedToIntFunction<T, E> of(CheckedToIntFunction<T, E> function)
    {
        return function;
    }
    
    static <T, E extends Throwable> CheckedToIntFunction<T, E> of(Catcher<Throwable> catcher, CheckedToIntFunction<T, E> function)
    {
        return function.catcher(catcher);
    }
    
    int applyAsIntOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default int applyAsInt(T t)
    {
        try { return applyAsIntOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedToIntFunction<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedToIntFunction<T, E>
        {
            CheckedToIntFunction<T, E> origin() { return CheckedToIntFunction.this; }
            
            @Override
            public int applyAsIntOrThrow(T t) throws E { return origin().applyAsIntOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
