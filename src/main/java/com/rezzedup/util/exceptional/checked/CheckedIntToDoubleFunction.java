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
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface CheckedIntToDoubleFunction<E extends Throwable>
    extends Catcher.Swap<CheckedIntToDoubleFunction<E>, Throwable>, IntToDoubleFunction
{
    static <E extends Throwable> CheckedIntToDoubleFunction<E> of(CheckedIntToDoubleFunction<E> function)
    {
        return function;
    }
    
    static <E extends Throwable> CheckedIntToDoubleFunction<E> of(Catcher<Throwable> catcher, CheckedIntToDoubleFunction<E> function)
    {
        return function.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(int value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default double applyAsDouble(int value)
    {
        try { return applyAsDoubleOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntToDoubleFunction<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntToDoubleFunction<E>
        {
            CheckedIntToDoubleFunction<E> origin() { return CheckedIntToDoubleFunction.this; }
            
            @Override
            public double applyAsDoubleOrThrow(int value) throws E
            {
                return origin().applyAsDoubleOrThrow(value);
            }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
