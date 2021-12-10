package com.github.michalszynkiewicz.kmp;

public class BoundaryFinder {

    final int[] jumpTable;
    final char[] pattern;

    int posInPattern = -1;

    public BoundaryFinder(String pattern) {
        this.pattern = pattern.toCharArray();
        this.jumpTable = jumpTable(this.pattern);
    }

    public boolean addAndCheck(char c) {
        while (posInPattern != -1 && c != pattern[posInPattern + 1]) {
            posInPattern = jumpTable[posInPattern];
        }
        if (c == pattern[posInPattern + 1]) {
            posInPattern++;
            return posInPattern == pattern.length - 1;
        }

        return false;
    }

    static int[] jumpTable(char[] pattern) {
        if (pattern == null) {
            throw new NullPointerException("Pattern cannot be null");
        }
        int[] result = new int[pattern.length];
        int jumpToPos = -1;
        result[0] = -1;
        for (int i = 1; i < pattern.length; i++) {
            jumpToPos++;
            while (pattern[jumpToPos] != pattern[i]) {
                if (jumpToPos == 0) {
                    jumpToPos = -1;
                    break;
                }
                jumpToPos = result[jumpToPos - 1] + 1;
            }
            result[i] = jumpToPos;
        }
        return result;
    }
}
