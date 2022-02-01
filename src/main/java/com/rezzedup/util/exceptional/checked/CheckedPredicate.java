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
import java.util.function.Predicate;

@FunctionalInterface
public interface CheckedPredicate<T, E extends Throwable>
    extends CheckedFunctionalInterface<CheckedPredicate<T, E>, E>, Predicate<T>
{
    static <T, E extends Throwable> CheckedPredicate<T, E> of(CheckedPredicate<T, E> predicate)
    {
        return predicate;
    }
    
    static <T, E extends Throwable> CheckedPredicate<T, E> of(Catcher<Throwable> catcher, CheckedPredicate<T, E> predicate)
    {
        return predicate.catcher(catcher);
    }
    
    boolean testOrThrow(T t) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default boolean test(T t)
    {
        try { return testOrThrow(t); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedPredicate<T, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _E> implements CheckedPredicate<T, E>
        {
            CheckedPredicate<T, E> origin() { return CheckedPredicate.this; }
            
            @Override
            public boolean testOrThrow(T t) throws E { return origin().testOrThrow(t); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
