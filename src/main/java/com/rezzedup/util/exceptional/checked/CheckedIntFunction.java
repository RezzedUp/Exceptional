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
import java.util.function.IntFunction;

@FunctionalInterface
public interface CheckedIntFunction<R, E extends Throwable>
    extends Catcher.Swap<CheckedIntFunction<R, E>, Throwable>, IntFunction<R>
{
    static <R, E extends Throwable> CheckedIntFunction<R, E> of(CheckedIntFunction<R, E> function)
    {
        return function;
    }
    
    static <R, E extends Throwable> CheckedIntFunction<R, E> of(Catcher<Throwable> catcher, CheckedIntFunction<R, E> function)
    {
        return function.catcher(catcher);
    }
    
    R applyOrThrow(int value) throws E;
    
    @Override
    default @NullOr R apply(int value)
    {
        try { return applyOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return null;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntFunction<R, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_R, _E> implements CheckedIntFunction<R, E>
        {
            CheckedIntFunction<R, E> origin() { return CheckedIntFunction.this; }
            
            @Override
            public R applyOrThrow(int value) throws E { return origin().applyOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<R, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
