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
import java.util.function.LongConsumer;

@FunctionalInterface
public interface CheckedLongConsumer<E extends Throwable>
    extends Catcher.Swap<CheckedLongConsumer<E>, Throwable>, LongConsumer
{
    static <E extends Throwable> CheckedLongConsumer<E> of(CheckedLongConsumer<E> consumer)
    {
        return consumer;
    }
    
    static <E extends Throwable> CheckedLongConsumer<E> of(Catcher<Throwable> catcher, CheckedLongConsumer<E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    void acceptOrThrow(long value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default void accept(long value)
    {
        try { acceptOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongConsumer<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongConsumer<E>
        {
            CheckedLongConsumer<E> origin() { return CheckedLongConsumer.this; }
            
            @Override
            public void acceptOrThrow(long value) throws E { origin().acceptOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
