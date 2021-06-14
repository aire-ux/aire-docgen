package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.Parser;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.UnknownBlockTagTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import lombok.val;

public class GroupParser implements Parser {

  public static final Symbol GroupSymbol = new DefaultSymbol("group");

  @Override
  public boolean appliesTo(@Nonnull Element element, @Nullable DocTree tree) {
    return element.getKind() == ElementKind.CLASS
        && tree != null
        && tree.getKind() == Kind.UNKNOWN_BLOCK_TAG
        && ((UnknownBlockTagTree) tree).getTagName().equals("group");
  }

  @Override
  public SyntaxNode<DocTree, Element> parse(@Nonnull Element element, @Nullable DocTree tree) {
    val name = element.getSimpleName().toString();
    assert tree != null;
    return new NamedSyntaxNode<>(
        name,
        GroupSymbol,
        element,
        tree,
        Parsing.extractTextNodes(((UnknownBlockTagTree) tree).getContent()),
        new ArrayList<>(),
        new LinkedHashMap<>());
  }
}
