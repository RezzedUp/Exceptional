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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class CheckedInterfacesTests
{
    private static class CheckedInterfaces<T extends CheckedFunctionalInterface<T, IOException>>
    {
        // Unchecked JDK functional interfaces in: java.util.function
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/package-summary.html
        static final List<String> FUNCTIONAL_INTERFACES =
            List.of(
                "BiConsumer",
                "BiFunction",
                "BinaryOperator",
                "BiPredicate",
                "BooleanSupplier",
                "Consumer",
                "DoubleBinaryOperator",
                "DoubleConsumer",
                "DoubleFunction",
                "DoublePredicate",
                "DoubleSupplier",
                "DoubleToIntFunction",
                "DoubleToLongFunction",
                "DoubleUnaryOperator",
                "Function",
                "IntBinaryOperator",
                "IntConsumer",
                "IntFunction",
                "IntPredicate",
                "IntSupplier",
                "IntToDoubleFunction",
                "IntToLongFunction",
                "IntUnaryOperator",
                "LongBinaryOperator",
                "LongConsumer",
                "LongFunction",
                "LongPredicate",
                "LongSupplier",
                "LongToDoubleFunction",
                "LongToIntFunction",
                "LongUnaryOperator",
                "ObjDoubleConsumer",
                "ObjIntConsumer",
                "ObjLongConsumer",
                "Predicate",
                "Supplier",
                "ToDoubleBiFunction",
                "ToDoubleFunction",
                "ToIntBiFunction",
                "ToIntFunction",
                "ToLongBiFunction",
                "ToLongFunction",
                "UnaryOperator"
            );
        
        static final Set<Class<?>> TESTED = new HashSet<>();
        
        private final Class<?> type;
        private final T thing;
        
        static <T extends CheckedFunctionalInterface<T, IOException>> CheckedInterfaces<T> test(T thing)
        {
            Objects.requireNonNull(thing, "thing");
            Class<?> type = thing.getClass().getInterfaces()[0];
            
            // Only test a specific checked functional interface once per run.
            assertTrue(
                TESTED.add(type),
                () -> "Already tested checked functional interface: " + thing.getClass().getSimpleName()
            );
            
            System.out.println("Testing checked functional interface: " + type.getSimpleName());
            
            CheckedInterfaces<T> test = new CheckedInterfaces<>(type, thing);
            test.internalImplSwapsCatcher();
            return test;
        }
        
        private CheckedInterfaces(Class<?> type, T thing)
        {
            this.type = type;
            this.thing = thing;
        }
        
        private Supplier<String> fail(String message)
        {
            return () -> message.replace("%name%", type.getSimpleName());
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
    
    //@AfterAll //TODO: add tests for all other checked interfaces then enable this:
    public static void allFunctionalInterfacesHaveCheckedCounterparts()
    {
        Set<String> checkedNames =
            CheckedInterfaces.TESTED.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        
        List<String> missingCounterparts =
            CheckedInterfaces.FUNCTIONAL_INTERFACES.stream()
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
    public void testCheckedBinaryOperator()
    {
        CheckedInterfaces.test(CheckedBinaryOperator.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow("a", "b"))
            .throwsUnchecked(unchecked -> unchecked.apply("a", "b"));
    }
    
    @Test
    public void testCheckedBiPredicate()
    {
        CheckedInterfaces.test(CheckedBiPredicate.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.testOrThrow("a", "b"))
            .throwsUnchecked(unchecked -> unchecked.test("a", "b"));
    }
    
    @Test
    public void testCheckedBooleanSupplier()
    {
        CheckedInterfaces.test(CheckedBooleanSupplier.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedBooleanSupplier::getAsBooleanOrThrow)
            .throwsUnchecked(CheckedBooleanSupplier::getAsBoolean);
    }
    
    @Test
    public void testCheckedConsumer()
    {
        CheckedInterfaces.test(CheckedConsumer.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow("a"))
            .throwsUnchecked(unchecked -> unchecked.accept("a"));
    }
    
    @Test
    public void testCheckedDoubleBinaryOperator()
    {
        CheckedInterfaces.test(CheckedDoubleBinaryOperator.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsDoubleOrThrow(1.0, 2.0))
            .throwsUnchecked(unchecked -> unchecked.applyAsDouble(1.0, 2.0));
    }
    
    @Test
    public void testCheckedDoubleConsumer()
    {
        CheckedInterfaces.test(CheckedDoubleConsumer.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.accept(1.0));
    }
    
    @Test
    public void testCheckedDoubleFunction()
    {
        CheckedInterfaces.test(CheckedDoubleFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.apply(1.0));
    }
    
    @Test
    public void testCheckedDoublePredicate()
    {
        CheckedInterfaces.test(CheckedDoublePredicate.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.testOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.test(1.0));
    }
    
    @Test
    public void testCheckedDoubleSupplier()
    {
        CheckedInterfaces.test(CheckedDoubleSupplier.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedDoubleSupplier::getAsDoubleOrThrow)
            .throwsUnchecked(CheckedDoubleSupplier::getAsDouble);
    }
    
    @Test
    public void testCheckedDoubleToIntFunction()
    {
        CheckedInterfaces.test(CheckedDoubleToIntFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsIntOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.applyAsInt(1.0));
    }
    
    @Test
    public void testCheckedDoubleToLongFunction()
    {
        CheckedInterfaces.test(CheckedDoubleToLongFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsLongOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.applyAsLong(1.0));
    }
    
    @Test
    public void testCheckedDoubleUnaryOperator()
    {
        CheckedInterfaces.test(CheckedDoubleUnaryOperator.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsDoubleOrThrow(1.0))
            .throwsUnchecked(unchecked -> unchecked.applyAsDouble(1.0));
    }
    
    @Test
    public void testCheckedFunction()
    {
        CheckedInterfaces.test(CheckedFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow("a"))
            .throwsUnchecked(unchecked -> unchecked.apply("a"));
    }
    
    @Test
    public void testCheckedIntBinaryOperator()
    {
        CheckedInterfaces.test(CheckedIntBinaryOperator.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsIntOrThrow(1, 2))
            .throwsUnchecked(unchecked -> unchecked.applyAsInt(1, 2));
    }
    
    @Test
    public void testCheckedIntConsumer()
    {
        CheckedInterfaces.test(CheckedIntConsumer.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.accept(1));
    }
    
    @Test
    public void testCheckedIntFunction()
    {
        CheckedInterfaces.test(CheckedIntFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.apply(1));
    }
    
    @Test
    public void testCheckedIntPredicate()
    {
        CheckedInterfaces.test(CheckedIntPredicate.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.testOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.test(1));
    }
    
    @Test
    public void testCheckedIntSupplier()
    {
        CheckedInterfaces.test(CheckedIntSupplier.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedIntSupplier::getAsIntOrThrow)
            .throwsUnchecked(CheckedIntSupplier::getAsInt);
    }
    
    @Test
    public void testCheckedIntToDoubleFunction()
    {
        CheckedInterfaces.test(CheckedIntToDoubleFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsDoubleOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.applyAsDouble(1));
    }
    
    @Test
    public void testCheckedIntToLongFunction()
    {
        CheckedInterfaces.test(CheckedIntToLongFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsLongOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.applyAsLong(1));
    }
    
    @Test
    public void testCheckedIntUnaryOperator()
    {
        CheckedInterfaces.test(CheckedIntUnaryOperator.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsIntOrThrow(1))
            .throwsUnchecked(unchecked -> unchecked.applyAsInt(1));
    }
    
    @Test
    public void testCheckedLongBinaryOperator()
    {
        CheckedInterfaces.test(CheckedLongBinaryOperator.of((a, b) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsLongOrThrow(1L, 2L))
            .throwsUnchecked(unchecked -> unchecked.applyAsLong(1L, 2L));
    }
    
    @Test
    public void testCheckedLongConsumer()
    {
        CheckedInterfaces.test(CheckedLongConsumer.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.acceptOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.accept(1L));
    }
    
    @Test
    public void testCheckedLongFunction()
    {
        CheckedInterfaces.test(CheckedLongFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.apply(1L));
    }
    
    @Test
    public void testCheckedLongPredicate()
    {
        CheckedInterfaces.test(CheckedLongPredicate.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.testOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.test(1L));
    }
    
    @Test
    public void testCheckedLongSupplier()
    {
        CheckedInterfaces.test(CheckedLongSupplier.of(() -> { throw new IOException(); }))
            .throwsIoException(CheckedLongSupplier::getAsLongOrThrow)
            .throwsUnchecked(CheckedLongSupplier::getAsLong);
    }
    
    @Test
    public void testCheckedLongToDoubleFunction()
    {
        CheckedInterfaces.test(CheckedLongToDoubleFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsDoubleOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.applyAsDouble(1L));
    }
    
    @Test
    public void testCheckedLongToIntFunction()
    {
        CheckedInterfaces.test(CheckedLongToIntFunction.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsIntOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.applyAsInt(1L));
    }
    
    @Test
    public void testCheckedLongUnaryOperator()
    {
        CheckedInterfaces.test(CheckedLongUnaryOperator.of((a) -> { throw new IOException(); }))
            .throwsIoException(checked -> checked.applyAsLongOrThrow(1L))
            .throwsUnchecked(unchecked -> unchecked.applyAsLong(1L));
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
