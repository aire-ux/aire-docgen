package com.example.demo;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.aire.ux.test.spring.EnableSpring;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import lombok.val;
import org.springframework.boot.test.context.SpringBootTest;

@AireTest
@EnableSpring
@SpringBootTest
@Routes(scanClassPackage = MainView.class)
class StandardApplicationLayoutTest {


  @ViewTest
  @Navigate("")
  void ensureEventIsFired(@Select MainView mainView) {
    assertNotNull(mainView);
    val result = new AtomicBoolean(false);
    mainView.addNavigationStateChangeListener(event -> result.set(true));
    mainView.openNavigation();
    assertTrue(result.get());
  }

}