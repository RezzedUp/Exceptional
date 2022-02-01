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
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface CheckedIntToLongFunction<E extends Throwable>
    extends CheckedFunctionalInterface<CheckedIntToLongFunction<E>, E>, IntToLongFunction
{
    static <E extends Throwable> CheckedIntToLongFunction<E> of(CheckedIntToLongFunction<E> function)
    {
        return function;
    }
    
    static <E extends Throwable> CheckedIntToLongFunction<E> of(Catcher<Throwable> catcher, CheckedIntToLongFunction<E> function)
    {
        return function.catcher(catcher);
    }
    
    long applyAsLongOrThrow(int value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default long applyAsLong(int value)
    {
        try { return applyAsLongOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0L;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntToLongFunction<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntToLongFunction<E>
        {
            CheckedIntToLongFunction<E> origin() { return CheckedIntToLongFunction.this; }
            
            @Override
            public long applyAsLongOrThrow(int value) throws E
            {
                return origin().applyAsLongOrThrow(value);
            }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
