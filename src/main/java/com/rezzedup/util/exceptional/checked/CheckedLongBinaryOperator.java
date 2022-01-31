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
import java.util.function.LongBinaryOperator;

@FunctionalInterface
public interface CheckedLongBinaryOperator<E extends Throwable>
    extends Catcher.Swap<CheckedLongBinaryOperator<E>, Throwable>, LongBinaryOperator
{
    static <E extends Throwable> CheckedLongBinaryOperator<E> of(CheckedLongBinaryOperator<E> binaryOperator)
    {
        return binaryOperator;
    }
    
    static <E extends Throwable> CheckedLongBinaryOperator<E> of(Catcher<Throwable> catcher, CheckedLongBinaryOperator<E> binaryOperator)
    {
        return binaryOperator.catcher(catcher);
    }
    
    long applyAsLongOrThrow(long left, long right) throws E;
    
    @Override
    default long applyAsLong(long left, long right)
    {
        try { return applyAsLongOrThrow(left, right); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongBinaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongBinaryOperator<E>
        {
            CheckedLongBinaryOperator<E> origin() { return CheckedLongBinaryOperator.this; }
            
            @Override
            public long applyAsLongOrThrow(long left, long right) throws E
            {
                return origin().applyAsLongOrThrow(left, right);
            }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
