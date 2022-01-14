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

/**
 * {@code Runnable} that can throw checked exceptions.
 *
 * @param <E>   exception type
 *
 * @see Runnable
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Throwable> extends Catcher.Swap<Throwable, CheckedRunnable<E>>, Runnable
{
    static <E extends Throwable> CheckedRunnable<E> of(CheckedRunnable<E> runnable)
    {
        return runnable;
    }
    
    static <E extends Throwable> CheckedRunnable<E> of(Catcher<Throwable> catcher, CheckedRunnable<E> runnable)
    {
        return runnable.catcher(catcher);
    }
    
    /**
     * Runs.
     *
     * @throws E a checked exception
     */
    void runOrThrow() throws E;
    
    @Override
    default void run()
    {
        try { runOrThrow(); }
        catch (Throwable e) { catcher().handleSafely(e); }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedRunnable<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedRunnable<E>
        {
            CheckedRunnable<E> origin() { return CheckedRunnable.this; }
    
            @Override
            public void runOrThrow() throws E { origin().runOrThrow(); }
    
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
