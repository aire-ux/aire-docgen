package com.aire.ux.docgen;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.docgen.parsers.ComponentElementParser;
import com.aire.ux.docgen.parsers.PropertyParser;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.SyntaxNode;
import io.sunshower.lang.tuple.Pair;
import java.net.URI;
import java.util.NoSuchElementException;
import lombok.val;
import org.junit.jupiter.api.Test;

public class AireDocletTest {

  @SuppressWarnings("unchecked")
  static ProcessingContext parse(String componentName, String contents) {
    val uri = URI.create(format("mem://java/%s.java", componentName));
    return AireDocumentationManager.parse(uri, contents, Pair.of("-d", DocletTests.testOutput()));
  }

  @Test
  void ensureParsingSimpleComponentElementWorks() {
    val type = "/**\n" + "* @component\n" + "*/\n" + "public class Component {\n" + "}\n";

    val result = parse("Component", type);
    assertNotNull(result.getSyntaxTree());
    SyntaxNode node = (SyntaxNode) result.getSyntaxTree().getRoot().getChildren().get(0);
    assertEquals(node.getSymbol(), ComponentElementParser.ComponentElement);
    assertEquals(((NamedSyntaxNode) node).getName(), "Component");
  }

  @Test
  void ensureContentWithNoNameForComponentIsCorrect() {
    val type =
        "/**\n"
            + "* @component\n"
            + "* this is just some content\n"
            + "*/\n"
            + "public class Component {\n"
            + "}\n";

    val result = parse("Component", type);
    assertNotNull(result.getSyntaxTree());
    val node = (NamedSyntaxNode) result.getSyntaxTree().getRoot().getChildren().get(0);
    assertEquals(node.getSymbol(), ComponentElementParser.ComponentElement);
    assertEquals(node.getName(), "Component");
    assertEquals(node.getContent().trim(), "this is just some content");
  }

  @Test
  void ensureCodeNodeUnderComponentProducesNoCodeChildrenForEmptyBlock() {

    val type =
        "/**\n"
            + "* @component\n"
            + "* this is just some content\n"
            + "* <code>\n"
            + "*\n"
            + "* </code>\n"
            + "*/\n"
            + "public class Component {\n"
            + "}\n";
    val result = parse("Component", type);
    SyntaxNode node = (SyntaxNode) result.getSyntaxTree().getRoot().getChildren().get(0);
    assertTrue(node.hasChildren());
  }

  @Test
  void ensureCodeNodeUnderComponentProducesNoCodeChildrenForValidBlock() {

    val type =
        "/**\n"
            + "* @component\n"
            + "* this is just some content\n"
            + "* <code lang=\"java\">\n"
            + "*   public void sayHello() {\n"
            + "*   }\n"
            + "* </code>\n"
            + "*/\n"
            + "public class Component {\n"
            + "}\n";
    val result = parse("Component", type);
    val nodes = result.findAll(node -> node.getSymbol() == ComponentElementParser.CodeElement);
    assertEquals(nodes.size(), 1);
    val codeNode = (NamedSyntaxNode) nodes.get(0);
    assertEquals(codeNode.getName(), "java");
  }

  @Test
  void ensureExtractingMultipleCodeBlocksWorks() {
    val type =
        "/**\n"
            + "* @component\n"
            + "* this is just some content\n"
            + "* <code lang=\"java\">\n"
            + "*   public void sayHello() {\n"
            + "*   }\n"
            + "* </code>\n"
            + "* <code lang=\"groovy\">\n"
            + "*   public void sayHello() {\n"
            + "*   }\n"
            + "* </code>\n"
            + "*/\n"
            + "public class Component {\n"
            + "}\n";
    val result = parse("Component", type);
    val nodes = result.findAll(node -> node.getSymbol() == ComponentElementParser.CodeElement);
    assertEquals(nodes.size(), 2);
    var codeNode = (NamedSyntaxNode) nodes.get(0);
    assertEquals("java", codeNode.getName());
    codeNode = (NamedSyntaxNode) nodes.get(1);
    assertEquals("groovy", codeNode.getName());
  }

  @Test
  void ensureProperiesOnFieldsAreParsed() {

    val test =
        "public class Component {\n"
            + "\n"
            + "  /**\n"
            + "  * @property\n"
            + "  *\n"
            + "  *\n"
            + "  */\n"
            + "  private String field;\n"
            + "}\n";

    val result = parse("Component", test);
    val nodes = result.findAll(node -> node.getSymbol() == PropertyParser.PropertySymbol);
    assertEquals(1, nodes.size());
    val node = (NamedSyntaxNode) nodes.get(0);
    assertEquals("java.lang.String", node.getProperty("type"));
    assertEquals("field", node.getName());
  }

  @Test
  void ensurePropertyIsChildOfClass() {
    val test =
        "/**\n"
            + "* @component\n"
            + "* <code lang=\"groovy\">\n"
            + "*    public void doStuff();\n"
            + "* </code>\n"
            + "*/\n"
            + "public class Component {\n"
            + "\n"
            + "  /**\n"
            + "  * @property\n"
            + "  *\n"
            + "  *\n"
            + "  */\n"
            + "  private String field;\n"
            + "}\n";
    val result = parse("Component", test);
    val prop =
        result
            .findFirst(e -> e.getSymbol() == ComponentElementParser.ComponentElement)
            .orElseThrow(() -> new NoSuchElementException("not here"));

    val field =
        result
            .findFirst(e -> e.getSymbol() == PropertyParser.PropertySymbol)
            .orElseThrow(() -> new NoSuchElementException("not here"));
    assertTrue(prop.getChildren().contains(field));
  }

  @Test
  void ensurePropertyHasCorrectComments() {
    val test =
        "/**\n"
            + "* @component\n"
            + "* <code lang=\"groovy\">\n"
            + "*    public void doStuff();\n"
            + "* </code>\n"
            + "*/\n"
            + "package test;\n"
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
            + "}\n";
    val result = parse("Component", test);

    val field =
        result
            .findFirst(e -> e.getSymbol() == PropertyParser.PropertySymbol)
            .orElseThrow(() -> new NoSuchElementException("not here"));
    assertTrue(field.getContent().contains("awesome property"));
  }

  @Test
  void ensureGroupComponentWorks() {
    val test =
        "/**\n"
            + "* @group whatever\n"
            + "* @component\n"
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

  @Test
  void ensureNestedComponentsWork() {
    val test =
        "/**\n"
            + "* @component\n"
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

    val field =
        result
            .findFirst(e -> e.getSymbol() == PropertyParser.PropertySymbol)
            .orElseThrow(() -> new NoSuchElementException("not here"));
    System.out.println(result.getSyntaxTree());
  }
}
