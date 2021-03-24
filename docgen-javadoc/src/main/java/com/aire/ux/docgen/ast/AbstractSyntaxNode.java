package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;

public class AbstractSyntaxNode implements SyntaxNode {

  final Symbol symbol;
  final Element source;
  final DocTree comment;
  final List<SyntaxNode> children;

  private String content;

  public AbstractSyntaxNode(Symbol symbol, Element source, DocTree comment) {
    this(symbol, source, null, comment, new ArrayList<>());
  }

  /**
   * @param symbol  the associated symbol (element type)
   * @param source  the language element this was retrieved from
   * @param content the String content (if any)
   * @param comment the actual comment node (if any)
   */
  public AbstractSyntaxNode(Symbol symbol, Element source, String content, DocTree comment) {
    this(symbol, source, content, comment, new ArrayList<>());
  }

  public AbstractSyntaxNode(Symbol symbol, Element source, String content,
      DocTree comment, List<SyntaxNode> children) {
    this.symbol = symbol;
    this.source = source;
    this.content = content;
    this.comment = comment;
    this.children = children;
  }


  @Override
  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public DocTree getComment() {
    return comment;
  }

  @Override
  public Element getSource() {
    return source;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public List<SyntaxNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  @Override
  public boolean addChild(SyntaxNode child) {
    return children.add(child);
  }
}
