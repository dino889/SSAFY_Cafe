package com.ssafy.cafe.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ssafy.cafe.model.service.UserService;

@SpringBootTest
public class PasswordEncoderTest {
  
   @Autowired
   private UserService userService;

   @Autowired
   private PasswordEncoder passwordEncoder;
  
   @Test
   @DisplayName("패스워드 암호화 테스트")
   void passwordEncode() {
      // given
      String rawPassword = "12345678";

      // when
      String encodedPassword = passwordEncoder.encode(rawPassword);

      // then
      assertAll(
            () -> assertNotEquals(rawPassword, encodedPassword),
            () -> assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
      );
   } 
}