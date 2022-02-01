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
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CheckedIntBinaryOperator<E extends Throwable>
    extends Catcher.Swap<CheckedIntBinaryOperator<E>, Throwable>, IntBinaryOperator
{
    static <E extends Throwable> CheckedIntBinaryOperator<E> of(CheckedIntBinaryOperator<E> binaryOperator)
    {
        return binaryOperator;
    }
    
    static <E extends Throwable> CheckedIntBinaryOperator<E> of(Catcher<Throwable> catcher, CheckedIntBinaryOperator<E> binaryOperator)
    {
        return binaryOperator.catcher(catcher);
    }
    
    int applyAsIntOrThrow(int left, int right) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default int applyAsInt(int left, int right)
    {
        try { return applyAsIntOrThrow(left, right); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntBinaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntBinaryOperator<E>
        {
            CheckedIntBinaryOperator<E> origin() { return CheckedIntBinaryOperator.this; }
            
            @Override
            public int applyAsIntOrThrow(int left, int right) throws E
            {
                return origin().applyAsIntOrThrow(left, right);
            }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
