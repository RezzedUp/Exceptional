/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;
import com.rezzedup.util.exceptional.Rethrow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class CheckedInterfacesTests
{
    private static class CheckedInterfaces<T extends CheckedFunctionalInterface<T, IOException>>
    {
        private static final Set<Class<?>> TESTED = new HashSet<>();
        
        private final T thing;
        
        static <T extends CheckedFunctionalInterface<T, IOException>> CheckedInterfaces<T> test(T thing)
        {
            // Only test a class once per run.
            assertTrue(
                TESTED.add((thing.getClass())),
                () -> "Already tested class: " + thing.getClass().getSimpleName()
            );
            
            CheckedInterfaces<T> test = new CheckedInterfaces<>(Objects.requireNonNull(thing, "thing"));
            test.internalImplSwapsCatcher();
            return test;
        }
        
        private CheckedInterfaces(T thing)
        {
            this.thing = thing;
        }
        
        private Supplier<String> fail(String message)
        {
            return () -> message.replace("%name%", thing.getClass().getSimpleName());
        }
        
        private void internalImplSwapsCatcher()
        {
            Catcher<Throwable> catcher = Catcher::ignore;
            T swapped = thing.catcher(catcher);
            
            // Original catcher should obviously be an entirely different instance.
            assertNotSame(catcher, thing.catcher());
            
            // Catcher should be the same exact instance in the swapped version.
            assertSame(catcher, swapped.catcher());
            
            // Make sure that if the same catcher is received, the 'swappable' simply returns itself.
            assertSame(swapped, swapped.catcher(catcher));
            
            T again = swapped.catcher(Catcher::print);
            
            // Ensure that a new 'swappable' instance is returned when a different catcher is received, however.
            assertNotSame(swapped, again);
            
            String originToString = "origin=" + thing;
            
            // Both 'swapped' and 'again' should have the same origin
            assertTrue(swapped.toString().contains(originToString));
            assertTrue(again.toString().contains(originToString));
        }
        
        CheckedInterfaces<T> throwsIoException(CheckedConsumer<T, IOException> checked)
        {
            assertThrows(
                IOException.class,
                () -> checked.acceptOrThrow(thing),
                fail("%name% did not throw an IOException when executing its checked action")
            );
            
            return this;
        }
        
        @SuppressWarnings("UnusedReturnValue")
        CheckedInterfaces<T> throwsUnchecked(Consumer<T> unchecked)
        {
            Assertions.assertThrows(
                Rethrow.class,
                () -> unchecked.accept(thing),
                fail("%name% did not throw a Rethrow when executing its unchecked action")
            );
            
            Assertions.assertDoesNotThrow(
                () -> unchecked.accept(thing.catcher(Catcher::ignore)),
                fail("%name% threw an exception despite swapping the catcher with 'ignore'")
            );
            
            return this;
        }
    }
    
    @Test
    public void testCheckedBiConsumer()
    {
        CheckedInterfaces.test(CheckedBiConsumer.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow("a", "b"))
            .throwsUnchecked(unchecked -> unchecked.accept("a", "b"));
    }
    
    @Test
    public void testCheckedBiFunction()
    {
        CheckedInterfaces.test(CheckedBiFunction.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow("a", "b"))
            .throwsUnchecked(unchecked -> unchecked.apply("a", "b"));
    }
    
    @Test
    public void testCheckedConsumer()
    {
        CheckedInterfaces.test(CheckedConsumer.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow("a"))
            .throwsUnchecked(unchecked -> unchecked.accept("a"));
    }
    
    @Test
    public void testCheckedFunction()
    {
        CheckedInterfaces.test(CheckedFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow("a"))
            .throwsUnchecked(unchecked -> unchecked.apply("a"));
    }
    
    @Test
    public void testCheckedRunnable()
    {
        CheckedInterfaces.test(CheckedRunnable.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedRunnable::runOrThrow)
            .throwsUnchecked(CheckedRunnable::run);
    }
    
    @Test
    public void testCheckedSupplier()
    {
        CheckedSupplier<String, IOException> supplier = () -> { throw new IOException(); };
        
        CheckedInterfaces.test(CheckedSupplier.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedSupplier::getOrThrow)
            .throwsUnchecked(CheckedSupplier::get);
    }
}
