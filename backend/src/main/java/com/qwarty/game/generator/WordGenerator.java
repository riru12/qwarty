package com.qwarty.game.generator;

import java.util.List;
import java.util.Random;

public class WordGenerator {

    private static final List<String> WORDS = List.of(
        "apple", "banana", "orange", "grape", "peach",
        "table", "chair", "screen", "keyboard", "mouse",
        "river", "mountain", "forest", "cloud", "storm"
    );

    private static final Random RANDOM = new Random();

    public static String randomWord() {
        return WORDS.get(RANDOM.nextInt(WORDS.size()));
    }

}
