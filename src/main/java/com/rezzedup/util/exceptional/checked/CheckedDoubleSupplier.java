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
import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface CheckedDoubleSupplier<E extends Throwable>
    extends Catcher.Swap<CheckedDoubleSupplier<E>, Throwable>, DoubleSupplier
{
    static <E extends Throwable> CheckedDoubleSupplier<E> of(CheckedDoubleSupplier<E> supplier)
    {
        return supplier;
    }
    
    static <E extends Throwable> CheckedDoubleSupplier<E> of(Catcher<Throwable> catcher, CheckedDoubleSupplier<E> supplier)
    {
        return supplier.catcher(catcher);
    }
    
    double getAsDoubleOrThrow() throws E;
    
    @Override
    default double getAsDouble()
    {
        try { return getAsDoubleOrThrow(); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoubleSupplier<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleSupplier<E>
        {
            CheckedDoubleSupplier<E> origin() { return CheckedDoubleSupplier.this; }
            
            @Override
            public double getAsDoubleOrThrow() throws E { return origin().getAsDoubleOrThrow(); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
