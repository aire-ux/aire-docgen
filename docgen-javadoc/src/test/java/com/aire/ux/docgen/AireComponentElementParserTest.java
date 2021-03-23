package com.aire.ux.docgen;

import static com.aire.ux.docgen.DocletTests.loadFromClassPath;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintWriter;
import lombok.val;
import org.junit.jupiter.api.Test;

public class AireComponentElementParserTest {


  @Test
  void ensureElementParserExtractsElementClass() {
    val objs = loadFromClassPath("airedocs/airedocs/TestClass2.java");
    val result = AireDocumentationManager.parse(new PrintWriter(System.out), objs);

    assertNotNull(result);
  }


}
