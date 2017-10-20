package ru.gusevanton.telegramnotificationservice.service;

import org.junit.Test;

/**
 * Created by antongusev on 17.10.17.
 */
public class RegistrationServiceTest {

    @Test
    public void generateCodeTest() {
         RegistrationService registrationService = new RegistrationService();
         String code = registrationService.generateCode.get();
         assert code.length() == 6;
    }

}