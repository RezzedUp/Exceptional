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
import java.util.function.IntConsumer;

@FunctionalInterface
public interface CheckedIntConsumer<E extends Throwable>
    extends Catcher.Swap<CheckedIntConsumer<E>, Throwable>, IntConsumer
{
    static <E extends Throwable> CheckedIntConsumer<E> of(CheckedIntConsumer<E> consumer)
    {
        return consumer;
    }
    
    static <E extends Throwable> CheckedIntConsumer<E> of(Catcher<Throwable> catcher, CheckedIntConsumer<E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    void acceptOrThrow(int value) throws E;
    
    @Override
    default void accept(int value)
    {
        try { acceptOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntConsumer<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntConsumer<E>
        {
            CheckedIntConsumer<E> origin() { return CheckedIntConsumer.this; }
            
            @Override
            public void acceptOrThrow(int value) throws E { origin().acceptOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
