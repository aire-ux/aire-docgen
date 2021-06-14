package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Parser;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.UnknownBlockTagTree;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import lombok.val;

public class ComponentElementParser implements Parser {

  @SuppressFBWarnings public static final Symbol CodeElement = new DefaultSymbol("code");
  @SuppressFBWarnings public static final Symbol ComponentElement = new DefaultSymbol("component");

  @Override
  public boolean appliesTo(@Nonnull Element element, DocTree tree) {
    return element.getKind() == ElementKind.CLASS
        && tree != null
        && tree.getKind() == Kind.UNKNOWN_BLOCK_TAG
        && ((UnknownBlockTagTree) tree).getTagName().equals("component");
  }

  @Override
  public SyntaxNode<DocTree, Element> parse(@Nonnull Element element, @Nonnull DocTree tree) {
    val name = element.getSimpleName().toString();
    val toRemove = new HashSet<DocTree>();
    val children = new CodeNodeVisitor(element).visit(tree, toRemove);
    val content = rewrite(toRemove, (UnknownBlockTagTree) tree);
    return new NamedSyntaxNode<>(
        name, ComponentElement, element, tree, content, children, new LinkedHashMap<>());
  }

  private String rewrite(HashSet<DocTree> toRemove, UnknownBlockTagTree tree) {
    val iter = tree.getContent().iterator();
    val textualContent = new StringBuilder();
    while (iter.hasNext()) {
      val child = iter.next();
      if (!toRemove.contains(child) && child.getKind() == Kind.TEXT) {
        textualContent.append(child);
      }
    }
    return textualContent.toString();
  }
}
