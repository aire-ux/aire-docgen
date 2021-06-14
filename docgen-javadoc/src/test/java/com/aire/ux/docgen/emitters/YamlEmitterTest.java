package com.aire.ux.docgen.emitters;

import static java.lang.String.format;

import com.aire.ux.docgen.AireDocumentationManager;
import com.aire.ux.docgen.DocletTests;
import com.aire.ux.docgen.ProcessingContext;
import io.sunshower.lang.tuple.Pair;
import java.net.URI;
import lombok.val;
import org.junit.jupiter.api.Test;

class YamlEmitterTest {


  @SuppressWarnings("unchecked")
  static ProcessingContext parse(String componentName, String contents) {
    val uri = URI.create(format("mem://java/%s.java", componentName));
    return AireDocumentationManager
        .parse(uri, contents, Pair.of("-d", DocletTests.testOutput() + "/documentation"),
            Pair.of("--format", "yaml"));
  }


  @Test
  @SuppressWarnings("unchecked")
  void ensureWritingCompleteComponentWorks() {
    val fileObjects = DocletTests.hierarchyIn("resources/test/airedocs");
    AireDocumentationManager.parse(fileObjects, Pair.of("-d", DocletTests.testOutput() + "/docs"),
        Pair.of("--format", "yaml"));

  }

  @Test
  void ensureGroupComponentWorks() {
    val test =
        "package test;\n"
        + "/**\n"
        + "* @group whatever\n"
        + "* @component\n"
        + "* this component \n"
        + "* is awesome. Thanks for writing it!\n"
        + "* *hello*\n"
        + "* <code lang=\"groovy\">\n"
        + "*    public void doStuff();\n"
        + "* </code>\n"
        + "*/\n"
        + "public class Component {\n"
        + "\n"
        + "  /**\n"
        + "  * @property\n"
        + "  *\n"
        + "  * this is an awesome property\n"
        + "  * don't you think?\n"
        + "  *\n"
        + "  *\n"
        + "  */\n"
        + "  private String field;\n"
        + "\n"
        + "  /**\n"
        + "  * @group test\n"
        + "  * @component\n"
        + "  * hello world\n"
        + "  * <code lang=\"java\">\n"
        + "  *  public void doStuff();\n"
        + "  * </code>\n"
        + "  */\n"
        + "  class A {\n"
        + "  }\n"
        + "  /**\n"
        + "  * @component\n"
        + "  * hello world\n"
        + "  * <code lang=\"java\">\n"
        + "  *  public void doStuff1();\n"
        + "  * </code>\n"
        + "  */\n"
        + "  class B {\n"
        + "    /**\n"
        + "    * @group coolio\n"
        + "    * @component\n"
        + "    * hello world\n"
        + "    * <code lang=\"java\">\n"
        + "    *  public void doStuff2();\n"
        + "    * </code>\n"
        + "    */\n"
        + "    class A {\n"
        + "      /**\n"
        + "      * @property\n"
        + "      */\n"
        + "      private int sup;\n"
        + "    }\n"
        + "  }\n"
        + "}\n";
    val result = parse("Component", test);
    System.out.println(result.getSyntaxTree());
  }
}