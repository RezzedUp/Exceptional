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
import java.util.function.BiFunction;

/**
 * {@code BiFunction} that can throw checked exceptions.
 *
 * @param <T>   first argument type
 * @param <U>   second argument type
 * @param <R>   return type
 * @param <E>   exception type
 *
 * @see BiFunction
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R, E extends Throwable>
    extends Catcher.Swap<CheckedBiFunction<T, U, R, E>, Throwable>, BiFunction<T, U, R>
{
    static <T, U, R, E extends Throwable> CheckedBiFunction<T, U, R, E> of(CheckedBiFunction<T, U, R, E> biFunction)
    {
        return biFunction;
    }
    
    static <T, U, R, E extends Throwable> CheckedBiFunction<T, U, R, E> of(Catcher<Throwable> catcher, CheckedBiFunction<T, U, R, E> biFunction)
    {
        return biFunction.catcher(catcher);
    }
    
    /**
     * Applies this function to the given arguments.
     *
     * @param t     the first argument
     * @param u     the second argument
     *
     * @return the function result
     * @throws E a checked exception
     */
    R applyOrThrow(T t, U u) throws E;
    
    @Override
    default @NullOr R apply(T t, U u)
    {
        try { return applyOrThrow(t, u); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return null;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedBiFunction<T, U, R, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _U, _R, _E> implements CheckedBiFunction<T, U, R, E>
        {
            CheckedBiFunction<T, U, R, E> origin() { return CheckedBiFunction.this; }
            
            @Override
            public R applyOrThrow(T t, U u) throws E { return origin().applyOrThrow(t, u); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, U, R, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
