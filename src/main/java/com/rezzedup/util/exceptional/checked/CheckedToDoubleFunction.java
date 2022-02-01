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
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface CheckedToDoubleFunction<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedToDoubleFunction<T, E>, E>, ToDoubleFunction<T>
{
    static <T, E extends Throwable> CheckedToDoubleFunction<T, E> of(CheckedToDoubleFunction<T, E> function)
    {
        return function;
    }
    
    static <T, E extends Throwable> CheckedToDoubleFunction<T, E> of(Catcher<Throwable> catcher, CheckedToDoubleFunction<T, E> function)
    {
        return function.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default double applyAsDouble(T t)
    {
        try { return applyAsDoubleOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedToDoubleFunction<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedToDoubleFunction<T, E>
        {
            CheckedToDoubleFunction<T, E> origin() { return CheckedToDoubleFunction.this; }
            
            @Override
            public double applyAsDoubleOrThrow(T t) throws E { return origin().applyAsDoubleOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
