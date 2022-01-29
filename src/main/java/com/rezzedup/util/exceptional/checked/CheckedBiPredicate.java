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
import java.util.function.BiPredicate;

@FunctionalInterface
public interface CheckedBiPredicate<T, U, E extends Throwable>
    extends Catcher.Swap<CheckedBiPredicate<T, U, E>, Throwable>, BiPredicate<T, U>
{
    static <T, U, E extends Throwable> CheckedBiPredicate<T, U, E> of(CheckedBiPredicate<T, U, E> biPredicate)
    {
        return biPredicate;
    }
    
    static <T, U, E extends Throwable> CheckedBiPredicate<T, U, E> of(Catcher<Throwable> catcher, CheckedBiPredicate<T, U, E> biPredicate)
    {
        return biPredicate.catcher(catcher);
    }
    
    boolean testOrThrow(T t, U u) throws E;
    
    @Override
    default boolean test(T t, U u)
    {
        try { return testOrThrow(t, u); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedBiPredicate<T, U, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _U, _E> implements CheckedBiPredicate<T, U, E>
        {
            CheckedBiPredicate<T, U, E> origin() { return CheckedBiPredicate.this; }
            
            @Override
            public boolean testOrThrow(T t, U u) throws E { return origin().testOrThrow(t, u); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, U, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
