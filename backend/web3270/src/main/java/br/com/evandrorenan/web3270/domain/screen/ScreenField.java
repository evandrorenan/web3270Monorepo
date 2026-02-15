package br.com.evandrorenan.web3270.domain.screen;

public record ScreenField(
    int start,
    int end,
    String text,
    boolean isProtected,
    boolean isHidden,
    boolean isHighIntensity,
    String color
) {}
