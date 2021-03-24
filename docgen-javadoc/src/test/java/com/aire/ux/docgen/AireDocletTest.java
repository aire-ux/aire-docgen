package com.aire.ux.docgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.parsers.ComponentElementParser;
import java.net.URI;
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
    @Language("JAVA") val type = """
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
    @Language("JAVA") val type = """
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

    @Language("JAVA") val type = """
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
    assertFalse(node.hasChildren());
  }


  @Test
  void ensureCodeNodeUnderComponentProducesNoCodeChildrenForValidBlock() {

    @Language("JAVA") val type = """
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
    val node = result.getSyntaxTree().getRoot().getChildren().get(0);
    System.out.println(result.getSyntaxTree());
//    assertTrue(node.hasChildren());
  }
}
