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
import java.util.function.ToDoubleBiFunction;

@FunctionalInterface
public interface CheckedToDoubleBiFunction<T, U, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedToDoubleBiFunction<T, U, E>, E>, ToDoubleBiFunction<T, U>
{
    static <T, U, E extends Throwable> CheckedToDoubleBiFunction<T, U, E> of(CheckedToDoubleBiFunction<T, U, E> biFunction)
    {
        return biFunction;
    }
    
    static <T, U, E extends Throwable> CheckedToDoubleBiFunction<T, U, E> of(Catcher<Throwable> catcher, CheckedToDoubleBiFunction<T, U, E> biFunction)
    {
        return biFunction.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(T t, U u) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default double applyAsDouble(T t, U u)
    {
        try { return applyAsDoubleOrThrow(t, u); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedToDoubleBiFunction<T, U, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _U, _E> implements CheckedToDoubleBiFunction<T, U, E>
        {
            CheckedToDoubleBiFunction<T, U, E> origin() { return CheckedToDoubleBiFunction.this; }
            
            @Override
            public double applyAsDoubleOrThrow(T t, U u) throws E { return origin().applyAsDoubleOrThrow(t, u); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, U, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
