package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Context;
import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.UnknownBlockTagTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.val;

public class ComponentDocumentationParser extends AbstractBlockTagTreeParser {

  static final String TAG_NAME = "component";
  static final Function<DocTree, String> BlockTagTreeCacheKey =
      (DocTree node) -> (node instanceof BlockTagTree tree ? tree.getTagName() : node.toString());

  public ComponentDocumentationParser() {
    super(TAG_NAME);
  }

  @Override
  public boolean appliesTo(DocTree docTree) {
    if (docTree == null) {
      return false;
    }
    if (docTree.getKind() == Kind.DOC_COMMENT && docTree instanceof DocCommentTree tree) {
      for (val child : tree.getBlockTags()) {
        if (child != null
            && child.getKind() == Kind.UNKNOWN_BLOCK_TAG
            && child instanceof UnknownBlockTagTree tagTree) {
          return tagName.equals(tagTree.getTagName());
        }
      }
    }
    return false;
  }

  @Override
  public SyntaxNode parse(Context context) {
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

    val rootNode =
        new NamedSyntaxNode(
            root.getTagName(),
            Symbols.Component,
            root,
            collectContent(root),
            parseChildren(context, children));
    return rootNode;
  }

  private List<SyntaxNode> parseChildren(Context context, List<DocTree> children) {
    val result = new ArrayList<SyntaxNode>(children.size());
    for (val child : children) {
      val parser =
          context
              .resolve(BlockTagTreeCacheKey, child)
              .orElseThrow(() -> new ParsingException("Unrecognized node: " + child));
      val ctx = new Context(context.getParsers(), Collections.singletonList(child));
      result.add(parser.parse(ctx));
    }
    return result;
  }
}
