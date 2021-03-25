package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;

public interface Parser {

  boolean appliesTo(@Nonnull Element element, @Nullable DocTree tree);

  SyntaxNode parse(@Nonnull Element element, @Nullable DocTree tree);
}
