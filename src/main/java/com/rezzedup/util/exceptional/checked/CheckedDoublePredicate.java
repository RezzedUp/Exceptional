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
import java.util.function.DoublePredicate;

@FunctionalInterface
public interface CheckedDoublePredicate<E extends Throwable>
    extends Catcher.Swap<CheckedDoublePredicate<E>, Throwable>, DoublePredicate
{
    static <E extends Throwable> CheckedDoublePredicate<E> of(CheckedDoublePredicate<E> predicate)
    {
        return predicate;
    }
    
    static <E extends Throwable> CheckedDoublePredicate<E> of(Catcher<Throwable> catcher, CheckedDoublePredicate<E> predicate)
    {
        return predicate.catcher(catcher);
    }
    
    boolean testOrThrow(double value) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default boolean test(double value)
    {
        try { return testOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoublePredicate<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoublePredicate<E>
        {
            CheckedDoublePredicate<E> origin() { return CheckedDoublePredicate.this; }
            
            @Override
            public boolean testOrThrow(double value) throws E { return origin().testOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
