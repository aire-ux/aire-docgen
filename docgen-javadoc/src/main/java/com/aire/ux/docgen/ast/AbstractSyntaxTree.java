package com.aire.ux.docgen.ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.val;

public class AbstractSyntaxTree {

  static final Symbol ROOT_SYMBOL = new Symbol() {

    public String toString() {
      return "RootSymbol";
    }
  };
  @Getter
  private final SyntaxNode root;

  public AbstractSyntaxTree(SyntaxNode root) {
    this.root = root;
  }

  public AbstractSyntaxTree() {
    this(new RootSyntaxNode());
  }

  public String toString() {
    val os = new ByteArrayOutputStream();
    val result = new PrintStream(os);
    toString(
        root,
        result,
        "",
        true);
    return os.toString(StandardCharsets.UTF_8);
  }

  private void toString(SyntaxNode node, PrintStream out, String indent,
      boolean last) {

    if (node == root) {
      out.println(node);
    } else {
      out.println(indent + (last ? "└╴ " : "├╴") + node.toString());
    }

    indent = indent + (last ? "   " : "│  ");
    val iter = node.getChildren().iterator();
    while (iter.hasNext()) {
      val child = iter.next();
      val isLast = !iter.hasNext();
      toString(child, out, indent, isLast);
    }
  }

  static final class RootSyntaxNode extends AbstractSyntaxNode {

    public RootSyntaxNode() {
      super(ROOT_SYMBOL, null, null, null);
    }

    public String toString() {
      return "RootNode";
    }
  }
}
