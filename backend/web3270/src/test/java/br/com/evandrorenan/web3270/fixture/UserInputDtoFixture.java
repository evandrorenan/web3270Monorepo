package br.com.evandrorenan.web3270.fixture;

import java.util.ArrayList;
import java.util.List;

import br.com.evandrorenan.web3270.dto.SendKeysDto;
import br.com.evandrorenan.web3270.dto.UserInputDto;

public class UserInputDtoFixture {

    private UserInputDto userInputDto;

    private UserInputDtoFixture() {
        userInputDto = new UserInputDto();
        userInputDto.setSessionId("default-session-id");
        userInputDto.setSendKeys(new ArrayList<>());
    }

    public static UserInputDtoFixture get() {
        return new UserInputDtoFixture();
    }

    public UserInputDtoFixture withSessionId(String sessionId) {
        userInputDto.setSessionId(sessionId);
        return this;
    }

    public UserInputDtoFixture withSendKeys(List<SendKeysDto> sendKeys) {
        userInputDto.setSendKeys(sendKeys);
        return this;
    }
    
    public UserInputDtoFixture addSendKey(SendKeysDto sendKey) {
        userInputDto.getSendKeys().add(sendKey);
        return this;
    }

    public UserInputDto build() {
        return userInputDto;
    }
}
