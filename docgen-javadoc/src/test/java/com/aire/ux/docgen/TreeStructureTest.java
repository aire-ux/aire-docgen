package com.aire.ux.docgen;

import static com.aire.ux.docgen.DocletTests.loadFromClassPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.aire.ux.docgen.parsers.Symbols;
import java.io.PrintWriter;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TreeStructureTest {

  private AbstractSyntaxTree tree;
  private ProcessingContext processingContext;

  @BeforeEach
  void setUp() {
    val objs = loadFromClassPath("airedocs/airedocs/TestClass2.java");
    processingContext = AireDocumentationManager.parse(new PrintWriter(System.out), objs);
    tree = processingContext.getSyntaxTree();
  }

  @Test
  void treeMustHaveOneComponentAtRoot() {
    assertEquals(tree.getRoot().getSymbol(), Symbols.Component);
  }

  @Test
  void treeMustHaveNameAsChild() {
    val name =
        tree.getRoot().getChildren().stream()
            .filter(t -> t.getSymbol() == Symbols.Name)
            .findAny()
            .get();

    val content = name.getContent().trim();
    assertEquals(content, "Button");
  }
}
