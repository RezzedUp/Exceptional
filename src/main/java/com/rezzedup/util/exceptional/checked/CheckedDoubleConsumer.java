package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;

import java.util.Objects;
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface CheckedDoubleConsumer<E extends Throwable> extends DoubleConsumer
{
    static <E extends Throwable> CheckedDoubleConsumer<E> of(CheckedDoubleConsumer<E> consumer)
    {
        return consumer;
    }
    
    static <E extends Throwable> CheckedDoubleConsumer<E> of(Catcher<Throwable> catcher, CheckedDoubleConsumer<E> consumer)
    {
        return consumer.catcher(catcher);
    }
    
    void acceptOrThrow(double value) throws E;
    
    @Override
    default void accept(double value)
    {
        try { acceptOrThrow(value); }
        catch (Throwable e) { catcher().handleOrRethrowError(e); }
    }
    
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    default CheckedDoubleConsumer<E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_E> implements CheckedDoubleConsumer<E>
        {
            CheckedDoubleConsumer<E> origin() { return CheckedDoubleConsumer.this; }
            
            @Override
            public void acceptOrThrow(double value) throws E { origin().acceptOrThrow(value); }
            
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
