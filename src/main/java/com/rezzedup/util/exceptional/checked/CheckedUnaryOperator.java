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
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface CheckedUnaryOperator<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedUnaryOperator<T, E>, E>, UnaryOperator<T>
{
    static <T, E extends Throwable> CheckedUnaryOperator<T, E> of(CheckedUnaryOperator<T, E> unaryOperator)
    {
        return unaryOperator;
    }
    
    static <T, E extends Throwable> CheckedUnaryOperator<T, E> of(Catcher<Throwable> catcher, CheckedUnaryOperator<T, E> unaryOperator)
    {
        return unaryOperator.catcher(catcher);
    }
    
    T applyOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default @NullOr T apply(T t)
    {
        try { return applyOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return null;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedUnaryOperator<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedUnaryOperator<T, E>
        {
            CheckedUnaryOperator<T, E> origin() { return CheckedUnaryOperator.this; }
            
            @Override
            public T applyOrThrow(T t) throws E { return origin().applyOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
