package com.example.javalib;

public final class StringMath {
    private StringMath() {}

    public static int countOccurrences(String haystack, String needle) {
        if (haystack == null || needle == null) {
            throw new IllegalArgumentException("inputs are required");
        }
        if (needle.isEmpty()) {
            throw new IllegalArgumentException("needle must be non-empty");
        }
        int count = 0;
        int idx = 0;
        while (true) {
            int next = haystack.indexOf(needle, idx);
            if (next < 0) {
                return count;
            }
            count++;
            idx = next + needle.length();
        }
    }
}

