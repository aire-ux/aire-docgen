package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Context;
import com.aire.ux.docgen.Parser;
import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import java.util.Collections;
import lombok.val;

public abstract class AbstractBlockTagTreeParser implements Parser {

  final String tagName;

  protected AbstractBlockTagTreeParser(final String tagName) {
    this.tagName = tagName;
  }

  @Override
  public boolean appliesTo(DocTree docTree) {
    return docTree instanceof UnknownBlockTagTree tree && tagName.equals(tree.getTagName());
  }

  @Override
  public SyntaxNode parse(Context context) {
    for (val ctx : context.getTokens()) {
      if (ctx instanceof UnknownBlockTagTree tree) {
        return new NamedSyntaxNode(
            tree.getTagName(), Symbols.Name, tree, collectContent(tree), Collections.emptyList());
      }
    }
    return null;
  }

  protected String collectContent(UnknownBlockTagTree parent) {
    val result = new StringBuilder();
    for (val child : parent.getContent()) {
      if (child instanceof TextTree textTree) {
        result.append(textTree.getBody());
      }
    }
    return result.toString();
  }
}
