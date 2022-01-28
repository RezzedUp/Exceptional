package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
public interface CheckedDoubleBinaryOperator<E extends Throwable> extends DoubleBinaryOperator
{
    static <E extends Throwable> CheckedDoubleBinaryOperator<E> of(CheckedDoubleBinaryOperator<E> binaryOperator)
    {
        return binaryOperator;
    }
    
    static <E extends Throwable> CheckedDoubleBinaryOperator<E> of(Catcher<Throwable> catcher, CheckedDoubleBinaryOperator<E> binaryOperator)
    {
        return binaryOperator.catcher(catcher);
    }
    
    double applyAsDoubleOrThrow(double left, double right) throws E;
    
    @Override
    default double applyAsDouble(double left, double right)
    {
        try { return applyAsDoubleOrThrow(left, right); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
        return 0.0;
    }
    
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    default CheckedDoubleBinaryOperator<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleBinaryOperator<E>
        {
            CheckedDoubleBinaryOperator<E> origin() { return CheckedDoubleBinaryOperator.this; }
            
            @Override
            public double applyAsDoubleOrThrow(double left, double right) throws E
            {
                return origin().applyAsDoubleOrThrow(left, right);
            }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}