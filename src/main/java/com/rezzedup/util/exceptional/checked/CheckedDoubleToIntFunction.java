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
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface CheckedDoubleToIntFunction<E extends Throwable>
    extends Catcher.Swap<CheckedDoubleToIntFunction<E>, Throwable>, DoubleToIntFunction
{
    static <E extends Throwable> CheckedDoubleToIntFunction<E> of(CheckedDoubleToIntFunction<E> function)
    {
        return function;
    }
    
    static <E extends Throwable> CheckedDoubleToIntFunction<E> of(Catcher<Throwable> catcher, CheckedDoubleToIntFunction<E> function)
    {
        return function.catcher(catcher);
    }
    
    int applyAsIntOrThrow(double value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default int applyAsInt(double value)
    {
        try { return applyAsIntOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoubleToIntFunction<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleToIntFunction<E>
        {
            CheckedDoubleToIntFunction<E> origin() { return CheckedDoubleToIntFunction.this; }
            
            @Override
            public int applyAsIntOrThrow(double value) throws E { return origin().applyAsIntOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
