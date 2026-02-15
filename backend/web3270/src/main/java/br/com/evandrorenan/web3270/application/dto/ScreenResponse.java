package br.com.evandrorenan.web3270.application.dto;

import br.com.evandrorenan.web3270.domain.screen.Screen;
import br.com.evandrorenan.web3270.domain.screen.ScreenField;
import java.util.List;

public record ScreenResponse(
    String content,
    int cursorPosition,
    List<ScreenFieldResponse> fields
) {
    public static ScreenResponse from(Screen screen) {
        return new ScreenResponse(
            screen.getContent(),
            screen.getCursorPosition(),
            screen.getFields().stream()
                .map(ScreenFieldResponse::from)
                .toList()
        );
    }

    public record ScreenFieldResponse(
        int start,
        int end,
        String text,
        boolean isProtected,
        boolean isHidden,
        boolean isHighIntensity,
        String color
    ) {
        public static ScreenFieldResponse from(ScreenField field) {
            return new ScreenFieldResponse(
                field.start(),
                field.end(),
                field.text(),
                field.isProtected(),
                field.isHidden(),
                field.isHighIntensity(),
                field.color()
            );
        }
    }
}
