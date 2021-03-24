package com.aire.ux.docgen.ast;

import com.aire.ux.docgen.Context;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.val;

public class AbstractSyntaxTree {

  @Getter
  private final SyntaxNode root;

  @Getter
  private final Context context;

  public AbstractSyntaxTree(SyntaxNode root, Context context) {
    this.root = root;
    this.context = context;
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

    out.println(indent + (last ? "└╴ ": "├╴") + node.toString());

    indent = indent + (last ? "  " : "│  ");
    val iter = node.getChildren().iterator();
    while (iter.hasNext()) {
      val child = iter.next();
      val isLast = !iter.hasNext();
      toString(child, out, indent, isLast);
    }
  }
}
