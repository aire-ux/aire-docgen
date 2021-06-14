package com.aire.ux.docgen;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import io.sunshower.lambda.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.function.Predicate;
import javax.lang.model.element.Element;
import jdk.javadoc.doclet.Reporter;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class ProcessingContext {

  @Getter final Locale locale;
  @Getter final Reporter reporter;

  @Getter @Setter private AbstractSyntaxTree<DocTree, Element> syntaxTree;

  public ProcessingContext(Locale locale, Reporter reporter) {
    this.locale = locale;
    this.reporter = reporter;
  }

  /**
   * @param predicate the predicate to apply
   * @return the nodes, in order, satisfying the predicate
   */
  public List<SyntaxNode<DocTree, Element>> findAll(
      Predicate<SyntaxNode<DocTree, Element>> predicate) {
    if (syntaxTree == null) {
      return Collections.emptyList();
    }
    val result = new ArrayList<SyntaxNode<DocTree, Element>>();
    val node = syntaxTree.getRoot();
    val queue = new LinkedList<SyntaxNode<DocTree, Element>>();
    queue.offer(node);
    while (!queue.isEmpty()) {
      val n = queue.poll();
      if (predicate.test(n)) {
        result.add(n);
      }
      queue.addAll(n.getChildren());
    }
    return result;
  }

  public Option<SyntaxNode<DocTree, Element>> findFirst(
      Predicate<SyntaxNode<DocTree, Element>> predicate) {
    if (syntaxTree == null) {
      return Option.none();
    }

    val node = syntaxTree.getRoot();
    val stack = new Stack<SyntaxNode<DocTree, Element>>();
    stack.push(node);

    while (!stack.isEmpty()) {
      val n = stack.pop();
      if (predicate.test(n)) {
        return Option.some(n);
      }
      stack.addAll(n.getChildren());
    }
    return Option.none();
  }
}
