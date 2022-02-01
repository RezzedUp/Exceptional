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
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface CheckedDoubleFunction<R, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedDoubleFunction<R, E>, E>, DoubleFunction<R>
{
    static <R, E extends Throwable> CheckedDoubleFunction<R, E> of(CheckedDoubleFunction<R, E> function)
    {
        return function;
    }
    
    static <R, E extends Throwable> CheckedDoubleFunction<R, E> of(Catcher<Throwable> catcher, CheckedDoubleFunction<R, E> function)
    {
        return function.catcher(catcher);
    }
    
    R applyOrThrow(double value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default @NullOr R apply(double value)
    {
        try { return applyOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return null;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoubleFunction<R, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_R, _E> implements CheckedDoubleFunction<R, E>
        {
            CheckedDoubleFunction<R, E> origin() { return CheckedDoubleFunction.this; }
            
            @Override
            public R applyOrThrow(double value) throws E { return origin().applyOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<R, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
