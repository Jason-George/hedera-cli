package com.hedera.cli.hedera;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdjectivesWordListTest {

    @Test
    public void splitmystring() {
        String string = "zanyzealouszestyzigzag";
        String[] parts = string.split("(?=z)"); // split after the letter
//        String[] parts = string.split("(?<=c)"); // split before the letter

        // insert quotes
        List<String> wordList = new ArrayList<String>(Arrays.asList(parts));
        String res = String.join(",", wordList).replaceAll("([^,]+)", "\"$1\"");
        assertEquals(res, "\"zany\",\"zealous\",\"zesty\",\"zig\",\"zag\"");
    }
}