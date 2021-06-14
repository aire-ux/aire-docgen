package com.aire.ux.docgen.ast;

import com.aire.ux.docgen.parsers.ComponentElementParser;
import com.aire.ux.docgen.parsers.GroupParser;
import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import io.sunshower.lang.tuple.Pair;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.lang.model.element.Element;
import lombok.val;

public class GroupAssociatingSyntaxTreeRewriteRule implements
    SyntaxTreeRewriteRule<DocTree, Element> {

  @Override
  public AbstractSyntaxTree<DocTree, Element> rewrite(AbstractSyntaxTree<DocTree, Element> tree) {
    val node = tree.getRoot();
    return new AbstractSyntaxTree<>(rewrite(node));
  }

  private SyntaxNode<DocTree, Element> rewrite(SyntaxNode<DocTree, Element> node) {
    val componentAndIdx = locateChildAndIndex(node, ComponentElementParser.ComponentElement,
        n -> true);

    if (componentAndIdx.snd == -1) {
      val result = node.clone();
      for (val child : node.getChildren()) {
        result.addChild(rewrite(child));
      }
      return result;
    }

    val component = componentAndIdx.fst;

    val groupAndIdx = locateChildAndIndex(
        node, GroupParser.GroupSymbol,
        n -> Objects.equals(((NamedSyntaxNode<DocTree, Element>) n).getName(),
            ((NamedSyntaxNode<DocTree, Element>) component).getName())
    );

    if (groupAndIdx.snd == -1) {
      val result = node.clone();
      for (val child : node.getChildren()) {
        result.addChild(rewrite(child));
      }
      return result;
    }
    node.removeChild(groupAndIdx.snd);
    component.addChild(groupAndIdx.fst);

    val result = node.clone();
    for (val child : node.getChildren()) {
      result.addChild(rewrite(child));
    }

    return result;

  }

  private Pair<SyntaxNode<DocTree, Element>, Integer> locateChildAndIndex(
      SyntaxNode<DocTree, Element> node, Symbol symbol,
      Predicate<SyntaxNode<DocTree, Element>> predicate) {
    val children = node.getChildren();
    for (var i = 0; i < children.size(); i++) {
      val child = children.get(i);
      if (Objects.equals(child.getSymbol(), symbol) && predicate.test(child)) {
        return Pair.of(child, i);
      }
    }
    return Pair.of(null, -1);
  }


}
