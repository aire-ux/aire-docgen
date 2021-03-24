package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;
import lombok.Getter;

public class NamedSyntaxNode extends AbstractSyntaxNode {

  @Getter
  final String name;

  public NamedSyntaxNode(
      String name, Symbol symbol, DocTree source, String content, List<SyntaxNode> children) {
    super(symbol, source, content, children);
    this.name = name;
  }

  public String toString() {
    return """
        SyntaxNode[symbol:%s, name: %s]{%s}
        """.strip().formatted(symbol, name, content.replaceAll("\\n", " "));
  }
}
