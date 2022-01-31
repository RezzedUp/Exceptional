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
import java.util.function.LongPredicate;

@FunctionalInterface
public interface CheckedLongPredicate<E extends Throwable>
    extends Catcher.Swap<CheckedLongPredicate<E>, Throwable>, LongPredicate
{
    static <E extends Throwable> CheckedLongPredicate<E> of(CheckedLongPredicate<E> predicate)
    {
        return predicate;
    }
    
    static <E extends Throwable> CheckedLongPredicate<E> of(Catcher<Throwable> catcher, CheckedLongPredicate<E> predicate)
    {
        return predicate.catcher(catcher);
    }
    
    boolean testOrThrow(long value) throws E;
    
    @Override
    default boolean test(long value)
    {
        try { return testOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongPredicate<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongPredicate<E>
        {
            CheckedLongPredicate<E> origin() { return CheckedLongPredicate.this; }
            
            @Override
            public boolean testOrThrow(long value) throws E { return origin().testOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
