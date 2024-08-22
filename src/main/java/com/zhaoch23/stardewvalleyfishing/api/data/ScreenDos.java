package com.zhaoch23.stardewvalleyfishing.api.data;

import java.util.ArrayList;
import java.util.List;

public class ScreenDos {

    public final List<String> screen_open;
    public final List<String> screen_close;
    public final List<String> pulling_rod;
    public final List<String> releasing_rod;
    public final List<String> hooking_fish;
    public final List<String> fish_caught;
    public final List<String> perfect_fish_caught;
    public final List<String> fish_escaped;

    public ScreenDos(List<String> screen_open,
                     List<String> screen_close,
                     List<String> pulling_rod,
                     List<String> releasing_rod,
                     List<String> hooking_fish,
                     List<String> fish_caught,
                     List<String> perfect_fish_caught,
                     List<String> fish_escaped) {
        this.screen_open = screen_open;
        this.screen_close = screen_close;
        this.pulling_rod = pulling_rod;
        this.releasing_rod = releasing_rod;
        this.hooking_fish = hooking_fish;
        this.fish_caught = fish_caught;
        this.perfect_fish_caught = perfect_fish_caught;
        this.fish_escaped = fish_escaped;
    }

    public ScreenDos copy() {
        return new ScreenDos(
                new ArrayList<>(screen_open),
                new ArrayList<>(screen_close),
                new ArrayList<>(pulling_rod),
                new ArrayList<>(releasing_rod),
                new ArrayList<>(hooking_fish),
                new ArrayList<>(fish_caught),
                new ArrayList<>(perfect_fish_caught),
                new ArrayList<>(fish_escaped)
        );
    }
}
