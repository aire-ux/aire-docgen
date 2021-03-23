package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class AbstractSyntaxNode implements SyntaxNode {

  final Symbol symbol;
  final DocTree source;
  final String content;
  final List<SyntaxNode> children;

  public AbstractSyntaxNode(Symbol symbol, DocTree source, String content,
      List<SyntaxNode> children) {
    this.symbol = symbol;
    this.source = source;
    this.content = content;
    this.children = children;
  }


  @Override
  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public DocTree getSource() {
    return source;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public List<SyntaxNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public void addChild(@Nonnull SyntaxNode child) {
    children.add(child);
  }
}
