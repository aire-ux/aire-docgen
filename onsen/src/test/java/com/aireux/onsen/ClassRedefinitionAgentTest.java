package com.aireux.onsen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import lombok.val;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import transformed.PrivateNonFinalMember;

@TestInstance(Lifecycle.PER_CLASS)
class ClassRedefinitionAgentTest {

  private Instrumentation instrumentation;

  @SneakyThrows
  static byte[] readClass(String path) {
    val file = ClassLoader.getSystemResource(path);
    if (file == null) {
      throw new IllegalArgumentException("Error: path %s does not exist".formatted(path));
    }

    val actualFile = Paths.get(file.toURI());
    return Files.readAllBytes(actualFile);
  }

  @BeforeAll
  public void setUp() {
    ByteBuddyAgent.install();
    instrumentation = ByteBuddyAgent.getInstrumentation();
  }

  @Test
  void ensureInstrumentationCanInstrumentClasses() {
    assertTrue(instrumentation.isRedefineClassesSupported(), "must support class redefinition");
  }

  @Test
  void ensureObtainingAgentWorks() throws Exception {

    val inst = new PrivateNonFinalMember();
//    val inst =
//        (PrivateNonFinalMember)
//            Class.forName("transformed.PrivateNonFinalMember").getConstructor().newInstance();

    assertEquals("original", inst.getValue());
    val classDefinition =
        new ClassDefinition(
            PrivateNonFinalMember.class,
            readClass("test-classes/transformed/PrivateNonFinalMember.class"));
    instrumentation.redefineClasses(classDefinition);
    val newInstance =
        (PrivateNonFinalMember)
            Class.forName("transformed.PrivateNonFinalMember").getConstructor().newInstance();
    assertEquals("transformed", newInstance.getValue());

  }
}
