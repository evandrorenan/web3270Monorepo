package br.com.evandrorenan.web3270.domain.screen;

import java.util.List;

public class Screen {
    private final String content;
    private final int cursorPosition;
    private final List<ScreenField> fields;

    public Screen(String content, int cursorPosition, List<ScreenField> fields) {
        this.content = content;
        this.cursorPosition = cursorPosition;
        this.fields = fields;
    }

    public String getContent() {
        return content;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public List<ScreenField> getFields() {
        return fields;
    }
}
