package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.sun.source.doctree.DocTree;

public interface Parser {

  boolean appliesTo(DocTree docTree);

  AbstractSyntaxTree parse(Context context);

}
