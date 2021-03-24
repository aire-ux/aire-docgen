package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Context;
import com.aire.ux.docgen.Parser;
import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import io.sunshower.lang.tuple.Pair;
import java.util.Collections;
import java.util.List;
import lombok.val;

public abstract class AbstractBlockTagTreeParser implements Parser {

  final String tagName;
  final boolean codeBlockAware;

  protected AbstractBlockTagTreeParser(final String tagName) {
    this(tagName, true);
  }

  /**
   * @param tagName        the tag-name to process
   * @param codeBlockAware process the content
   */
  protected AbstractBlockTagTreeParser(final String tagName, final boolean codeBlockAware) {
    this.tagName = tagName;
    this.codeBlockAware = codeBlockAware;
  }

  @Override
  public boolean appliesTo(DocTree docTree) {
    return docTree instanceof UnknownBlockTagTree tree && tagName.equals(tree.getTagName());
  }

  @Override
  public SyntaxNode parse(Context context) {
    for (val ctx : context.getTokens()) {
      if (ctx instanceof UnknownBlockTagTree tree) {
        val children = collectContent(tree);
        return new NamedSyntaxNode(
            tree.getTagName(), Symbols.Name, tree, children.fst, children.snd);
      }
    }
    return null;
  }

  protected Pair<String, List<SyntaxNode>> collectContent(UnknownBlockTagTree parent) {
    val result = new StringBuilder();
    for (val child : parent.getContent()) {
      if (child instanceof TextTree textTree) {
        result.append(textTree.getBody());
      } else {
        result.append(child.toString());
      }
    }
    if (codeBlockAware) {
      val parser = new CodeBlockParser(result, parent);
      val children = parser.parse();
      return Pair.of(parser.getExtractedContent().toString(), children);
    }
    return Pair.of(result.toString(), Collections.emptyList());

  }
}
