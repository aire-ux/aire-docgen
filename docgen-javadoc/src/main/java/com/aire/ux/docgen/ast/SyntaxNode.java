package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;
import javax.lang.model.element.Element;

public interface SyntaxNode {

  Symbol getSymbol();

  DocTree getComment();

  Element getSource();

  String getContent();

  List<SyntaxNode> getChildren();

  boolean hasChildren();

  boolean addChild(SyntaxNode child);

  void setContent(String content);


}
