/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class AttemptTests
{
	private static final IntSupplier ZERO = () -> 0;
	
	private int divideByZero() { return 1 / ZERO.getAsInt(); }
	
	private void throwsChecked() throws Exception { throw new Exception(); }
	
	private void expect(String message){ for (int i = 0; i < 3; i++) { System.err.println("\n" + message + "\n"); } }
	
	@Test
	public void testIgnoring()
	{
		Optional<Integer> divided = assertDoesNotThrow(() -> Attempt.ignoring(this::divideByZero));
		assertTrue(divided.isEmpty());
		
		assertDoesNotThrow(() -> Attempt.ignoring(this::throwsChecked));
	}
	
	@Test
	public void testPrinting()
	{
		expect("THE STACKTRACES BELOW ARE EXPECTED.");
		
		Optional<Integer> divided = assertDoesNotThrow(() -> Attempt.printing(this::divideByZero));
		assertTrue(divided.isEmpty());
		
		expect("THESE STACKTRACES ARE EXPECTED.");
		
		assertDoesNotThrow(() -> Attempt.printing(this::throwsChecked));
		
		expect("NO MORE STACKTRACES ARE EXPECTED.");
	}
	
	@Test
	public void testRethrow()
	{
		assertThrows(Rethrow.class, () -> Attempt.rethrowing(this::divideByZero));
		assertThrows(Rethrow.class, () -> Attempt.rethrowing(this::throwsChecked));
	}
}
