package com.aire.ux.docgen.ast;

import com.aire.ux.docgen.Context;
import lombok.Getter;

public class AbstractSyntaxTree {

  @Getter private final SyntaxNode root;

  @Getter private final Context context;

  public AbstractSyntaxTree(SyntaxNode root, Context context) {
    this.root = root;
    this.context = context;
  }
}
