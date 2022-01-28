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
import java.util.function.BinaryOperator;

@FunctionalInterface
public interface CheckedBinaryOperator<T, E extends Throwable> extends BinaryOperator<T>
{
    static <T, E extends Throwable> CheckedBinaryOperator<T, E> of(CheckedBinaryOperator<T, E> binaryOperator)
    {
        return binaryOperator;
    }
    
    static <T, E extends Throwable> CheckedBinaryOperator<T, E> of(Catcher<Throwable> catcher, CheckedBinaryOperator<T, E> binaryOperator)
    {
        return binaryOperator.catcher(catcher);
    }
    
    T applyOrThrow(T t, T t2) throws E;
    
    @Override
    default @NullOr T apply(T t, T t2)
    {
        try { return applyOrThrow(t, t2); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return null;
    }
    
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    default CheckedBinaryOperator<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedBinaryOperator<T, E>
        {
            CheckedBinaryOperator<T, E> origin() { return CheckedBinaryOperator.this; }
            
            @Override
            public T applyOrThrow(T t, T t2) throws E { return origin().applyOrThrow(t, t2); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
