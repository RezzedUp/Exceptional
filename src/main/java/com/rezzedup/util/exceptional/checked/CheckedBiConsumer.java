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
import java.util.function.BiConsumer;

/**
 * {@code BiConsumer} that can throw checked exceptions.
 *
 * @param <T>   first argument type
 * @param <U>   second argument type
 * @param <E>   exception type
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Throwable>
    extends Catcher.Swap<CheckedBiConsumer<T, U, E>, Throwable>, BiConsumer<T, U>
{
    static <T, U, E extends Throwable> CheckedBiConsumer<T, U, E> of(CheckedBiConsumer<T, U, E> biConsumer)
    {
        return biConsumer;
    }
    
    static <T, U, E extends Throwable> CheckedBiConsumer<T, U, E> of(Catcher<Throwable> catcher, CheckedBiConsumer<T, U, E> biConsumer)
    {
        return biConsumer.catcher(catcher);
    }
    
    /**
     * Performs this operation on the given arguments.
     *
     * @param t     the first input argument
     * @param u     the second input argument
     *
     * @throws E a checked exception
     */
    void acceptOrThrow(T t, U u) throws E;
    
    @Override
    default void accept(T t, U u)
    {
        try { acceptOrThrow(t, u); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedBiConsumer<T, U, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _U, _E> implements CheckedBiConsumer<T, U, E>
        {
            CheckedBiConsumer<T, U, E> origin() { return CheckedBiConsumer.this; }
            
            @Override
            public void acceptOrThrow(T t, U u) throws E { origin().acceptOrThrow(t, u); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, U, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
