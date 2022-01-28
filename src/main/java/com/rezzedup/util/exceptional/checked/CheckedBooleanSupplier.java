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
import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface CheckedBooleanSupplier<E extends Throwable> extends BooleanSupplier
{
    static <E extends Throwable> CheckedBooleanSupplier<E> of(CheckedBooleanSupplier<E> supplier)
    {
        return supplier;
    }
    
    static <E extends Throwable> CheckedBooleanSupplier<E> of(Catcher<Throwable> catcher, CheckedBooleanSupplier<E> supplier)
    {
        return supplier.catcher(catcher);
    }
    
    boolean getAsBooleanOrThrow() throws E;
    
    @Override
    default boolean getAsBoolean()
    {
        try { return getAsBooleanOrThrow(); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return false;
    }
    
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    default CheckedBooleanSupplier<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedBooleanSupplier<E>
        {
            CheckedBooleanSupplier<E> origin() { return CheckedBooleanSupplier.this; }
            
            @Override
            public boolean getAsBooleanOrThrow() throws E { return origin().getAsBooleanOrThrow(); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
