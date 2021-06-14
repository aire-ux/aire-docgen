package com.aire.ux.docgen.ast;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;

@FunctionalInterface
public interface SyntaxTreeRewriteRule<T, U> {

  AbstractSyntaxTree<T, U> rewrite(AbstractSyntaxTree<T, U> tree);



}
