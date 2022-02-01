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
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface CheckedDoubleConsumer<E extends Throwable>
    extends Catcher.Swap<CheckedDoubleConsumer<E>, Throwable>, DoubleConsumer
{
    static <E extends Throwable> CheckedDoubleConsumer<E> of(CheckedDoubleConsumer<E> consumer)
    {
        return consumer;
    }
    
    static <E extends Throwable> CheckedDoubleConsumer<E> of(Catcher<Throwable> catcher, CheckedDoubleConsumer<E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    void acceptOrThrow(double value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default void accept(double value)
    {
        try { acceptOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoubleConsumer<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleConsumer<E>
        {
            CheckedDoubleConsumer<E> origin() { return CheckedDoubleConsumer.this; }
            
            @Override
            public void acceptOrThrow(double value) throws E { origin().acceptOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
