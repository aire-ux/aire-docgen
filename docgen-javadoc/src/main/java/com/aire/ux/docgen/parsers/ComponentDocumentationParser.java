package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Context;
import com.aire.ux.docgen.Parser;
import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        if (child != null
            && child.getKind() == Kind.UNKNOWN_BLOCK_TAG
            && child instanceof UnknownBlockTagTree) {
          return TAG_NAME.equals(((UnknownBlockTagTree) child).getTagName());
        }
      }
    }
    return false;
  }

  @Override
  public AbstractSyntaxTree parse(Context context) {
    val tokens = context.getTokens();
    val iterator = tokens.iterator();
    val children = new ArrayList<DocTree>(tokens.size() - 1);
    UnknownBlockTagTree root = null;
    while (iterator.hasNext()) {
      val token = iterator.next();
      if (token instanceof UnknownBlockTagTree comment) {
        if (TAG_NAME.equals(comment.getTagName())) {
          root = comment;
          continue;
        }
      }
      children.add(token);
    }
    if (root == null) {
      throw new ParsingException("Error: assertion that element of type <root> existed was false");
    }

    val rootNode = new NamedSyntaxNode(
        root.getTagName(),
        Symbols.Component,
        root,
        collectContent(root),
        parseChildren(children)
    );

    return new AbstractSyntaxTree(rootNode, context);
  }


  private List<SyntaxNode> parseChildren(List<DocTree> children) {
    return Collections.emptyList();
  }


  String collectContent(UnknownBlockTagTree parent) {
    val result = new StringBuilder();
    for (val child : parent.getContent()) {
      if (child instanceof TextTree textTree) {
        result.append(textTree.getBody());
      }
    }
    return result.toString();
  }
}
