package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;

public interface SyntaxNode {

  Symbol getSymbol();
  DocTree getSource();
  String getContent();
  List<SyntaxNode> getChildren();



//  private Symbol symbol,
//  DocTree element,
//  String content,
//  List<SyntaxNode> children

}
