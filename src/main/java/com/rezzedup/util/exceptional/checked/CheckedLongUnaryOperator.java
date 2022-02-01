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
import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface CheckedLongUnaryOperator<E extends Throwable>
    extends CheckedFunctionalInterface<CheckedLongUnaryOperator<E>, E>, LongUnaryOperator
{
    static <E extends Throwable> CheckedLongUnaryOperator<E> of(CheckedLongUnaryOperator<E> unaryOperator)
    {
        return unaryOperator;
    }
    
    static <E extends Throwable> CheckedLongUnaryOperator<E> of(Catcher<Throwable> catcher, CheckedLongUnaryOperator<E> unaryOperator)
    {
        return unaryOperator.catcher(catcher);
    }
    
    long applyAsLongOrThrow(long operand) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default long applyAsLong(long operand)
    {
        try { return applyAsLongOrThrow(operand); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0L;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedLongUnaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedLongUnaryOperator<E>
        {
            CheckedLongUnaryOperator<E> origin() { return CheckedLongUnaryOperator.this; }
            
            @Override
            public long applyAsLongOrThrow(long operand) throws E { return origin().applyAsLongOrThrow(operand); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
