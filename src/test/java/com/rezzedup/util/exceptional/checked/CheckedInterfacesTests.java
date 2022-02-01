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
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
                TESTED.add((thing.getClass().getInterfaces()[0])),
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
            assertThrows(
                Rethrow.class,
                () -> unchecked.accept(thing),
                fail("%name% did not throw a Rethrow when executing its unchecked counterpart")
            );
            
            assertDoesNotThrow(
                () -> unchecked.accept(thing.catcher(Catcher::ignore)),
                fail("%name% threw an exception despite swapping the catcher with 'ignore'")
            );
            
            return this;
        }
    }
    
    private static final Reflections REFLECTIONS;
    
    static
    {
        // Apparently JUnit's class loader doesn't work well with Reflections.
        // Nor does the class loader swapping junk seem to work when done later so...
        // set everything up the very moment the class loads in this here static initializer!
        
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(CheckedFunction.class.getClassLoader());
        
        REFLECTIONS = new Reflections(
            new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath())
        );
        
        // Put the class loader back where we found it
        Thread.currentThread().setContextClassLoader(loader);
    }
    
    //@AfterAll TODO: add tests for all other checked interfaces then enable this:
    public static void allFunctionalInterfacesHaveCheckedCounterparts()
    {
        Set<String> checkedNames =
            CheckedInterfaces.TESTED.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        
        List<String> missingCounterparts =
            REFLECTIONS.getTypesAnnotatedWith(FunctionalInterface.class).stream()
                .filter(clazz -> clazz.getPackageName().equals("java.util.function"))
                .map(Class::getSimpleName)
                .filter(name -> !checkedNames.contains("Checked" + name))
                .collect(Collectors.toList());
        
        assertTrue(
            missingCounterparts.isEmpty(),
            () -> "Missing 'checked' counterparts to: " + missingCounterparts
        );
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
