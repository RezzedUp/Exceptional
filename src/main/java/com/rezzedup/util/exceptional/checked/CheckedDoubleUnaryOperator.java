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
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface CheckedDoubleUnaryOperator<E extends Throwable>
    extends Catcher.Swap<CheckedDoubleUnaryOperator<E>, Throwable>, DoubleUnaryOperator
{
    static <E extends Throwable> CheckedDoubleUnaryOperator<E> of(CheckedDoubleUnaryOperator<E> unaryOperator)
    {
        return unaryOperator;
    }
    
    static <E extends Throwable> CheckedDoubleUnaryOperator<E> of(Catcher<Throwable> catcher, CheckedDoubleUnaryOperator<E> unaryOperator)
    {
        return unaryOperator.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(double operand) throws E;
    
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    default double applyAsDouble(double operand)
    {
        try { return applyAsDoubleOrThrow(operand); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedDoubleUnaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleUnaryOperator<E>
        {
            CheckedDoubleUnaryOperator<E> origin() { return CheckedDoubleUnaryOperator.this; }
            
            @Override
            public double applyAsDoubleOrThrow(double operand) throws E { return origin().applyAsDoubleOrThrow(operand); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
