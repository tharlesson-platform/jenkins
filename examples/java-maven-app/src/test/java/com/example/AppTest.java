package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AppTest {
  @Test
  void sumShouldWork() {
    assertEquals(5, App.sum(2, 3));
  }
}
