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
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface CheckedLongToIntFunction<E extends Throwable>
    extends Catcher.Swap<CheckedLongToIntFunction<E>, Throwable>, LongToIntFunction
{
    static <E extends Throwable> CheckedLongToIntFunction<E> of(CheckedLongToIntFunction<E> function)
    {
        return function;
    }
    
    static <E extends Throwable> CheckedLongToIntFunction<E> of(Catcher<Throwable> catcher, CheckedLongToIntFunction<E> function)
    {
        return function.catcher(catcher);
    }
    
    int applyAsIntOrThrow(long value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default int applyAsInt(long value)
    {
        try { return applyAsIntOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongToIntFunction<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongToIntFunction<E>
        {
            CheckedLongToIntFunction<E> origin() { return CheckedLongToIntFunction.this; }
            
            @Override
            public int applyAsIntOrThrow(long value) throws E { return origin().applyAsIntOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
