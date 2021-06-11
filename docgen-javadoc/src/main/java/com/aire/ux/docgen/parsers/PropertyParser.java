package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Parser;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.UnknownBlockTagTree;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import lombok.val;

public class PropertyParser implements Parser {

  public static final Symbol PropertySymbol = new DefaultSymbol("property");

  @Override
  public boolean appliesTo(@Nonnull Element element, @Nullable DocTree tree) {
    return element.getKind() == ElementKind.FIELD && tree.getKind() == Kind.UNKNOWN_BLOCK_TAG;
  }

  @Override
  public SyntaxNode<DocTree, Element> parse(@Nonnull Element element, DocTree tree) {
    val variable = (VariableElement) element;
    val name = variable.getSimpleName().toString();
    val type = variable.asType().toString();
    val node =
        new NamedSyntaxNode<>(
            name,
            PropertySymbol,
            element,
            tree,
            Parsing.extractTextNodes(((UnknownBlockTagTree) tree).getContent()));
    node.setProperty("type", type);
    return node;
  }
}
