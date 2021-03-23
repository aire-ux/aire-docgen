package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Context;
import com.aire.ux.docgen.Parser;
import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.UnknownBlockTagTree;
import lombok.val;

public class ComponentDocumentationParser implements Parser {

  static final String TAG_NAME = "component";

  @Override
  public boolean appliesTo(DocTree docTree) {
    if (docTree == null) {
      return false;
    }
    if (docTree.getKind() == Kind.DOC_COMMENT && docTree instanceof DocCommentTree tree) {
      for (val child : tree.getBlockTags()) {
        if (child != null && child.getKind() == Kind.UNKNOWN_BLOCK_TAG
            && child instanceof UnknownBlockTagTree) {
          return TAG_NAME.equals(((UnknownBlockTagTree) child).getTagName());
        }
      }
    }
    return false;
  }

  @Override
  public AbstractSyntaxTree parse(Context context) {
    return null;
//    return new AbstractSyntaxTree(context)
  }
}
