package br.com.evandrorenan.web3270.fixture;

import br.com.evandrorenan.web3270.dto.SessionDto;

public class SessionDtoFixture {

    private SessionDto sessionDto;

    private SessionDtoFixture() {
        sessionDto = new SessionDto();
        sessionDto.setSessionId("default-session-id");
        sessionDto.setConnected(true);
    }

    public static SessionDtoFixture get() {
        return new SessionDtoFixture();
    }

    public SessionDtoFixture withSessionId(String sessionId) {
        sessionDto.setSessionId(sessionId);
        return this;
    }

    public SessionDtoFixture withConnected(boolean connected) {
        sessionDto.setConnected(connected);
        return this;
    }

    public SessionDto build() {
        return sessionDto;
    }
}
