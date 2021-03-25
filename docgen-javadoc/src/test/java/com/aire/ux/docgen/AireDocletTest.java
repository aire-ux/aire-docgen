package com.aire.ux.docgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.parsers.ComponentElementParser;
import com.aire.ux.docgen.parsers.PropertyParser;
import java.net.URI;
import java.util.NoSuchElementException;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;

public class AireDocletTest {

  static ProcessingContext parse(String componentName, String contents) {
    val uri = URI.create("mem://java/%s.java".formatted(componentName));
    return AireDocumentationManager.parse(uri, contents);
  }

  @Test
  void ensureParsingSimpleComponentElementWorks() {
    @Language("JAVA")
    val type =
        """
        /**
        * @component
        */
        public class Component {
        }
        """;

    val result = parse("Component", type);
    assertNotNull(result.getSyntaxTree());
    val node = result.getSyntaxTree().getRoot().getChildren().get(0);
    assertEquals(node.getSymbol(), ComponentElementParser.ComponentElement);
    assertEquals(((NamedSyntaxNode) node).getName(), "Component");
  }

  @Test
  void ensureContentWithNoNameForComponentIsCorrect() {
    @Language("JAVA")
    val type =
        """
        /**
        * @component
        * this is just some content
        */
        public class Component {
        }
        """;

    val result = parse("Component", type);
    assertNotNull(result.getSyntaxTree());
    val node = (NamedSyntaxNode) result.getSyntaxTree().getRoot().getChildren().get(0);
    assertEquals(node.getSymbol(), ComponentElementParser.ComponentElement);
    assertEquals(node.getName(), "Component");
    assertEquals(node.getContent().trim(), "this is just some content");
  }

  @Test
  void ensureCodeNodeUnderComponentProducesNoCodeChildrenForEmptyBlock() {

    @Language("JAVA")
    val type =
        """
        /**
        * @component
        * this is just some content
        * <code>
        *
        * </code>
        */
        public class Component {
        }
        """;
    val result = parse("Component", type);
    val node = result.getSyntaxTree().getRoot().getChildren().get(0);
    assertTrue(node.hasChildren());
  }

  @Test
  void ensureCodeNodeUnderComponentProducesNoCodeChildrenForValidBlock() {

    @Language("JAVA")
    val type =
        """
        /**
        * @component
        * this is just some content
        * <code lang="java">
        *   public void sayHello() {
        *   }
        * </code>
        */
        public class Component {
        }
        """;
    val result = parse("Component", type);
    val nodes = result.findAll(node -> node.getSymbol() == ComponentElementParser.CodeElement);
    assertEquals(nodes.size(), 1);
    val codeNode = (NamedSyntaxNode) nodes.get(0);
    assertEquals(codeNode.getName(), "java");
  }

  @Test
  void ensureExtractingMultipleCodeBlocksWorks() {
    @Language("JAVA")
    val type =
        """
        /**
        * @component
        * this is just some content
        * <code lang="java">
        *   public void sayHello() {
        *   }
        * </code>
        * <code lang="groovy">
        *   public void sayHello() {
        *   }
        * </code>
        */
        public class Component {
        }
        """;
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
        """
        public class Component {

          /**
          * @property
          *
          *
          */
          private String field;
        }
        """;

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
        """
        /**
        * @component
        * <code lang="groovy">
        *    public void doStuff();
        * </code>
        */
        public class Component {

          /**
          * @property
          *
          *
          */
          private String field;
        }
        """;
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
        """
        /**
        * @component
        * <code lang="groovy">
        *    public void doStuff();
        * </code>
        */
        public class Component {

          /**
          * @property
          *
          * this is an awesome property
          * don't you think?
          *
          *
          */
          private String field;
        }
        """;
    val result = parse("Component", test);

    val field =
        result
            .findFirst(e -> e.getSymbol() == PropertyParser.PropertySymbol)
            .orElseThrow(() -> new NoSuchElementException("not here"));
    assertTrue(field.getContent().contains("awesome property"));
  }

  @Test
  void ensureNestedComponentsWork() {
    val test =
        """
        /**
        * @component
        * <code lang="groovy">
        *    public void doStuff();
        * </code>
        */
        public class Component {

          /**
          * @property
          *
          * this is an awesome property
          * don't you think?
          *
          *
          */
          private String field;

          /**
          * @component
          * hello world
          * <code lang="java">
          *  public void doStuff();
          * </code>
          */
          class A {
          }
          /**
          * @component
          * hello world
          * <code lang="java">
          *  public void doStuff1();
          * </code>
          */
          class B {
            /**
            * @component
            * hello world
            * <code lang="java">
            *  public void doStuff2();
            * </code>
            */
            class A {
              /**
              * @property
              */
              private int sup;
            }
          }
        }
        """;
    val result = parse("Component", test);

    val field =
        result
            .findFirst(e -> e.getSymbol() == PropertyParser.PropertySymbol)
            .orElseThrow(() -> new NoSuchElementException("not here"));
    System.out.println(result.getSyntaxTree());
  }
}
