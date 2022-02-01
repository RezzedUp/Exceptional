package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadMeExampleTests
{
    @SuppressWarnings("unused")
    public void readMeExample()
    {
        // Automatically handle exceptions
        List<String> lines =
            Attempt.ignoring().get(() -> Files.readAllLines(Path.of("example.txt"))).orElseGet(List::of);
        
        // Create checked versions of all standard functional interfaces
        CheckedSupplier<List<String>, IOException> getFileLines = () -> Files.readAllLines(Path.of("example.txt"));
    }
    
    @Test
    public void testExample()
    {
        assertDoesNotThrow(this::readMeExample);
    }
}
