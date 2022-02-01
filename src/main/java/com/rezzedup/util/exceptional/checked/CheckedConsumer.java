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
import java.util.function.Consumer;

/**
 * {@code Consumer} that can throw checked exceptions.
 *
 * @param <T>   argument type
 * @param <E>   exception type
 *
 * @see Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable>
    extends Catcher.Swap<CheckedConsumer<T, E>, Throwable>, Consumer<T>
{
    static <T, E extends Throwable> CheckedConsumer<T, E> of(CheckedConsumer<T, E> consumer)
    {
        return consumer;
    }
    
    static <T, E extends Throwable> CheckedConsumer<T, E> of(Catcher<Throwable> catcher, CheckedConsumer<T, E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    /**
     * Performs this operation on the given argument.
     *
     * @param t     the argument
     *
     * @throws E a checked exception
     */
    void acceptOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default void accept(T t)
    {
        try { acceptOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedConsumer<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedConsumer<T, E>
        {
            CheckedConsumer<T, E> origin() { return CheckedConsumer.this; }
            
            @Override
            public void acceptOrThrow(T t) throws E { origin().acceptOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
