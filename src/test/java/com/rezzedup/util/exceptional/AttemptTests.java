/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttemptTests
{
    private static class Result<T>
    {
        @NullOr T result;
        
        void assertIfResultExists(Consumer<T> assertions)
        {
            if (result != null) { assertions.accept(result); }
        }
    }
    
    private static void expect(String message)
    {
        System.err.println("\n\n");
        for (int i = 0; i < 3; i++) { System.err.println(message); }
        System.err.println("\n\n");
    }
    
    // The exceptional math logic *is* the point!
    @SuppressWarnings({"divzero", "NumericOverflow"})
    private void test(Attempt attempt, Function<Class<? extends Throwable>, Consumer<Executable>> assertions)
    {
        // Test a checked runnable that simply throws an Exception
        {
            assertions.apply(Exception.class).accept(
                () -> attempt.run(() -> { throw new Exception(); })
            );
        }
        
        // Test a checked supplier that throws an IOException
        {
            Result<Optional<?>> maybe = new Result<>();
            
            assertions.apply(IOException.class).accept(() ->
                maybe.result = attempt.get(() -> { throw new IOException(); })
            );
            
            maybe.assertIfResultExists(optional -> assertTrue(optional.isEmpty()));
        }
        
        // Test a checked int supplier that throws an ArithmeticException (divides by zero)
        {
            Result<OptionalInt> maybe = new Result<>();
            
            assertions.apply(ArithmeticException.class).accept(() ->
                maybe.result = attempt.getAsInt(() -> 1 / 0)
            );
            
            maybe.assertIfResultExists(optional -> assertTrue(optional.isEmpty()));
        }
        
        // Test a checked long supplier that throws an ArithmeticException (divides by zero)
        {
            Result<OptionalLong> maybe = new Result<>();
            
            assertions.apply(ArithmeticException.class).accept(() ->
                maybe.result = attempt.getAsLong(() -> 1L / 0L)
            );
            
            maybe.assertIfResultExists(optional -> assertTrue(optional.isEmpty()));
        }
    }
    
    @Test
    public void testIgnoringAttempt()
    {
        test(Attempt.ignoring(), exception -> Assertions::assertDoesNotThrow);
    }
    
    @Test
    public void testPrintingAttempt()
    {
        expect("[!] THE STACKTRACES BELOW ARE EXPECTED (TESTING EXCEPTION PRINTING).");
        test(Attempt.printing(), exception -> Assertions::assertDoesNotThrow);
        expect("[!] THE STACKTRACES ABOVE WERE EXPECTED (TESTING EXCEPTION PRINTING).");
    }
    
    @Test
    public void testRethrowAttempt()
    {
        test(Attempt.rethrowing(), exception -> task -> assertThrows(Rethrow.class, task));
    }
    
    @Test
    public void testCustomizedSneakyAttempt()
    {
        test(Attempt.with(Sneaky::catcher), exception -> task -> assertThrows(exception, task));
    }
    
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public void customizedAttemptThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> Attempt.with(null));
    }
}
