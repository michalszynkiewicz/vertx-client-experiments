package com.github.michalszynkiewicz.kmp;

import org.junit.jupiter.api.Test;

import static com.github.michalszynkiewicz.kmp.BoundaryFinder.jumpTable;
import static org.assertj.core.api.Assertions.assertThat;

public class JumpTableTest {

    int _find(String text, String pattern) {
        int[] jumpTable = jumpTable(pattern.toCharArray());
        int posInPattern = -1;
        int posInText = 0;
        while (posInText < text.length()) {
            if (text.charAt(posInText) == pattern.charAt(posInPattern + 1)) {
                posInPattern ++;
                posInText ++;
                if (posInPattern == pattern.length() - 1) {
                    return posInText - pattern.length();
                }
            } else {
                if (posInPattern == -1) {
                    posInText ++;
                } else {
                    posInPattern = jumpTable[posInPattern];
                }
            }
        }

        return -1;
    }

    int find(String text, String pattern) {
        BoundaryFinder finder = new BoundaryFinder(pattern);
        for (int i = 0; i < text.length(); i++) {
            if (finder.addAndCheck(text.charAt(i))) {
                return i - pattern.length() + 1;
            }
        }
        return -1;
    }

    @Test
    void shouldCreateProperKmpJumpTable() {
        int[] expected = new int[]{-1, 0, -1, -1, -1, -1, 0, 1, 2, -1};
        assertThat(jumpTable("aaffeeaafd".toCharArray())).isEqualTo(expected);
    }

    @Test
    void shouldFindPattern() {
        assertThat(find("aabbaabbaabc", "a")).isEqualTo(0);
        assertThat(find("ccabcc", "ab")).isEqualTo(2);
        assertThat(find("aabbaabbaabc", "baab")).isEqualTo(3);
        assertThat(find("aabbaabbaabc", "abc")).isEqualTo(9);
        assertThat(find("aabbaabbaabc", "aabbaabbaabc")).isEqualTo(0);
        assertThat(find("aabbaabbaabc", "aabbaabaabc")).isEqualTo(-1);
        assertThat(find("aa", "abc")).isEqualTo(-1);
        assertThat(find("aa", "aaa")).isEqualTo(-1);
        assertThat(find("zxv", "zzzxv")).isEqualTo(-1);
        assertThat(find("zzzzxzzxzzzxv", "zxv")).isEqualTo(10);
        assertThat(find("zxcvzzzzxzzxzzzxv", "zxv")).isEqualTo(14);
        assertThat(find("zxvcvzzzzxzzxzzzxv", "zxv")).isEqualTo(0);
    }
}
