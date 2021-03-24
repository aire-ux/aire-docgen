package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;

public interface Parser {

  boolean appliesTo(DocTree docTree);

  SyntaxNode parse(Context context);
}
