package com.aire.ux.docgen.ast;

import com.sun.source.doctree.DocTree;
import java.util.List;

public interface SyntaxNode {

  DocTree getContent();

  List<SyntaxNode> getChildren();




}
