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
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface CheckedIntUnaryOperator<E extends Throwable>
    extends Catcher.Swap<CheckedIntUnaryOperator<E>, Throwable>, IntUnaryOperator
{
    static <E extends Throwable> CheckedIntUnaryOperator<E> of(CheckedIntUnaryOperator<E> unaryOperator)
    {
        return unaryOperator;
    }
    
    static <E extends Throwable> CheckedIntUnaryOperator<E> of(Catcher<Throwable> catcher, CheckedIntUnaryOperator<E> unaryOperator)
    {
        return unaryOperator.catcher(catcher);
    }
    
    int applyAsIntOrThrow(int operand) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default int applyAsInt(int operand)
    {
        try { return applyAsIntOrThrow(operand); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedIntUnaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedIntUnaryOperator<E>
        {
            CheckedIntUnaryOperator<E> origin() { return CheckedIntUnaryOperator.this; }
            
            @Override
            public int applyAsIntOrThrow(int operand) throws E { return origin().applyAsIntOrThrow(operand); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
