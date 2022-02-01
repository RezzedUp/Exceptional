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
import java.util.function.ObjLongConsumer;

@FunctionalInterface
public interface CheckedObjLongConsumer<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedObjLongConsumer<T, E>, E>, ObjLongConsumer<T>
{
    static <T, E extends Throwable> CheckedObjLongConsumer<T, E> of(CheckedObjLongConsumer<T, E> consumer)
    {
        return consumer;
    }
    
    static <T, E extends Throwable> CheckedObjLongConsumer<T, E> of(Catcher<Throwable> catcher, CheckedObjLongConsumer<T, E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    void acceptOrThrow(T t, long value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default void accept(T t, long value)
    {
        try { acceptOrThrow(t, value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedObjLongConsumer<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedObjLongConsumer<T, E>
        {
            CheckedObjLongConsumer<T, E> origin() { return CheckedObjLongConsumer.this; }
            
            @Override
            public void acceptOrThrow(T t, long value) throws E { origin().acceptOrThrow(t, value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}

