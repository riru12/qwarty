package com.qwarty.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Word {
    private String text;
    private boolean fresh;
}
