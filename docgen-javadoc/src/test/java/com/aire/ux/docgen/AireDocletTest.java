package com.aire.ux.docgen;

import static com.aire.ux.docgen.DocletTests.loadFromClassPath;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintWriter;
import lombok.val;
import org.junit.jupiter.api.Test;

class AireDocletTest {

  @Test
  void ensureInvokingJavadocOnSampleSimpleClassProducesNonNullDocumentationContext() {
    val result =
        AireDocumentationManager.parse(
            new PrintWriter(System.out), loadFromClassPath("airedocs/airedocs/TestClass2.java"));
    assertNotNull(result);
  }

  @Test
  void ensureResultingDocumentationContextHasCorrectPaths() {
    val objs = loadFromClassPath("airedocs/airedocs/TestClass2.java");
    val result = AireDocumentationManager.parse(new PrintWriter(System.out), objs);
    assertNotNull(result);
    assertNotNull(result.getSyntaxTree());
  }
}
