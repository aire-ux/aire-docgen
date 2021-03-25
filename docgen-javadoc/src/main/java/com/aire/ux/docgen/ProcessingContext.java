package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.aire.ux.docgen.ast.SyntaxNode;
import io.sunshower.lambda.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.function.Predicate;
import jdk.javadoc.doclet.Reporter;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class ProcessingContext {

  @Getter final Locale locale;
  @Getter final Reporter reporter;

  @Getter @Setter private AbstractSyntaxTree syntaxTree;

  public ProcessingContext(Locale locale, Reporter reporter) {
    this.locale = locale;
    this.reporter = reporter;
  }

  /**
   * @param predicate the predicate to apply
   * @return the nodes, in order, satisfying the predicate
   */
  public List<SyntaxNode> findAll(Predicate<SyntaxNode> predicate) {
    if (syntaxTree == null) {
      return Collections.emptyList();
    }
    val result = new ArrayList<SyntaxNode>();
    val node = syntaxTree.getRoot();

    val queue = new LinkedList<SyntaxNode>();
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

  public Option<SyntaxNode> findFirst(Predicate<SyntaxNode> predicate) {
    if (syntaxTree == null) {
      return Option.none();
    }

    val node = syntaxTree.getRoot();
    val stack = new Stack<SyntaxNode>();
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
