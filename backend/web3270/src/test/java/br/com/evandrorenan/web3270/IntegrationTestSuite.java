package br.com.evandrorenan.web3270;

import br.com.evandrorenan.web3270.presentation.api.v1.controller.SecurityIntegrationTest;
import br.com.evandrorenan.web3270.presentation.api.v1.controller.SessionIntegrationTest;
import br.com.evandrorenan.web3270.presentation.websocket.WebSocketIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Web3270 Integration Test Suite")
@SelectClasses({
    SessionIntegrationTest.class,
    WebSocketIntegrationTest.class,
    SecurityIntegrationTest.class
})
public class IntegrationTestSuite {
}
