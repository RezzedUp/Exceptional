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
import java.util.function.IntPredicate;

@FunctionalInterface
public interface CheckedIntPredicate<E extends Throwable>
    extends Catcher.Swap<CheckedIntPredicate<E>, Throwable>, IntPredicate
{
    static <E extends Throwable> CheckedIntPredicate<E> of(CheckedIntPredicate<E> predicate)
    {
        return predicate;
    }
    
    static <E extends Throwable> CheckedIntPredicate<E> of(Catcher<Throwable> catcher, CheckedIntPredicate<E> predicate)
    {
        return predicate.catcher(catcher);
    }
    
    boolean testOrThrow(int value) throws E;
    
    @Override
    default boolean test(int value)
    {
        try { return testOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntPredicate<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntPredicate<E>
        {
            CheckedIntPredicate<E> origin() { return CheckedIntPredicate.this; }
            
            @Override
            public boolean testOrThrow(int value) throws E { return origin().testOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
