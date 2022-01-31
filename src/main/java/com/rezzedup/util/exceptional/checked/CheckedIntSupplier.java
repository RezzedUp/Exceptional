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
import java.util.function.IntSupplier;

@FunctionalInterface
public interface CheckedIntSupplier<E extends Throwable>
    extends Catcher.Swap<CheckedIntSupplier<E>, Throwable>, IntSupplier
{
    static <E extends Throwable> CheckedIntSupplier<E> of(CheckedIntSupplier<E> supplier)
    {
        return supplier;
    }
    
    static <E extends Throwable> CheckedIntSupplier<E> of(Catcher<Throwable> catcher, CheckedIntSupplier<E> supplier)
    {
        return supplier.catcher(catcher);
    }
    
    int getAsIntOrThrow() throws E;
    
    @Override
    default int getAsInt()
    {
        try { return getAsIntOrThrow(); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntSupplier<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntSupplier<E>
        {
            CheckedIntSupplier<E> origin() { return CheckedIntSupplier.this; }
            
            @Override
            public int getAsIntOrThrow() throws E { return origin().getAsIntOrThrow(); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
