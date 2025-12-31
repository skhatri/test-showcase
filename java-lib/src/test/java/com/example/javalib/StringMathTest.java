package com.example.javalib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringMathTest {

    @Test
    void shouldCountOccurrences() {
        assertEquals(2, StringMath.countOccurrences("a-b-a-b", "a"));
        assertEquals(3, StringMath.countOccurrences("ababab", "ab"));
        assertEquals(0, StringMath.countOccurrences("ababab", "zz"));
    }

    @Test
    void shouldRejectEmptyNeedle() {
        assertThrows(IllegalArgumentException.class, () -> StringMath.countOccurrences("abc", ""));
    }
}

