package br.com.evandrorenan.web3270.fixture;

import java.util.ArrayList;
import java.util.List;

import br.com.evandrorenan.web3270.dto.FieldDto;
import br.com.evandrorenan.web3270.dto.ScreenDto;

public class ScreenDtoFixture {

    private ScreenDto screenDto;

    private ScreenDtoFixture() {
        screenDto = new ScreenDto();
        screenDto.setSessionId("default-session-id");
        screenDto.setCursorPos(0);
        screenDto.setFields(new ArrayList<>());
    }

    public static ScreenDtoFixture get() {
        return new ScreenDtoFixture();
    }

    public ScreenDtoFixture withSessionId(String sessionId) {
        screenDto.setSessionId(sessionId);
        return this;
    }

    public ScreenDtoFixture withCursorPos(int cursorPos) {
        screenDto.setCursorPos(cursorPos);
        return this;
    }

    public ScreenDtoFixture withFields(List<FieldDto> fields) {
        screenDto.setFields(fields);
        return this;
    }
    
    public ScreenDtoFixture addField(FieldDto field) {
        screenDto.getFields().add(field);
        return this;
    }

    public ScreenDto build() {
        return screenDto;
    }
}
