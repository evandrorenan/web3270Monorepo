package br.com.evandrorenan.web3270.fixture;

import br.com.evandrorenan.web3270.dto.SendKeysDto;

public class TestDataBuilder {

    public static SessionDtoFixture createSessionDto() {
        return SessionDtoFixture.get();
    }

    public static ScreenDtoFixture createScreenDto() {
        return ScreenDtoFixture.get();
    }

    public static UserInputDtoFixture createUserInputDto() {
        return UserInputDtoFixture.get();
    }
    
    public static SendKeysDto createSendKeysDto(String text, int row, int col) {
        SendKeysDto dto = new SendKeysDto();
        dto.setText(text);
        dto.setRow(row);
        dto.setCol(col);
        return dto;
    }
}
