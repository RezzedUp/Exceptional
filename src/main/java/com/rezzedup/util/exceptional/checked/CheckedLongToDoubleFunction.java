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
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface CheckedLongToDoubleFunction<E extends Throwable>
    extends Catcher.Swap<CheckedLongToDoubleFunction<E>, Throwable>, LongToDoubleFunction
{
    static <E extends Throwable> CheckedLongToDoubleFunction<E> of(CheckedLongToDoubleFunction<E> function)
    {
        return function;
    }
    
    static <E extends Throwable> CheckedLongToDoubleFunction<E> of(Catcher<Throwable> catcher, CheckedLongToDoubleFunction<E> function)
    {
        return function.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(long value) throws E;
    
    @Override
    default double applyAsDouble(long value)
    {
        try { return applyAsDoubleOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongToDoubleFunction<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongToDoubleFunction<E>
        {
            CheckedLongToDoubleFunction<E> origin() { return CheckedLongToDoubleFunction.this; }
            
            @Override
            public double applyAsDoubleOrThrow(long value) throws E { return origin().applyAsDoubleOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
