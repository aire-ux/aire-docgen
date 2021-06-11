package com.aire.ux.docgen.parsers;

import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.SimpleDocTreeVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.lang.model.element.Element;
import lombok.val;

public class CodeNodeVisitor
    extends SimpleDocTreeVisitor<List<SyntaxNode<DocTree, Element>>, Set<DocTree>> {

  private final Element element;
  private final Stack<String> textualContent;
  private final Stack<SyntaxNode<DocTree, Element>> nodes;

  CodeNodeVisitor(final Element element) {
    this.element = element;
    this.nodes = new Stack<>();
    this.textualContent = new Stack<>();
  }

  @Override
  public List<SyntaxNode<DocTree, Element>> visitText(TextTree node, Set<DocTree> unused) {
    if (!nodes.isEmpty()) {
      val codeNode = nodes.peek();
      val content = codeNode.getContent();
      if (content == null) {
        codeNode.setContent(node.toString());
      } else {
        codeNode.setContent(codeNode.getContent() + node);
      }
      unused.add(node);
    }
    return Collections.emptyList();
  }

  @Override
  public List<SyntaxNode<DocTree, Element>> visitAttribute(
      AttributeTree node, Set<DocTree> docTrees) {
    val name = node.getName().toString();
    if ("lang".equals(name)) {
      textualContent.push(node.getValue().get(0).toString());
    }
    return Collections.emptyList();
  }

  @Override
  public List<SyntaxNode<DocTree, Element>> visitStartElement(
      StartElementTree node, Set<DocTree> toRemove) {
    val name = node.getName().toString();
    if ("code".equals(name)) {
      toRemove.add(node);
      this.visit(node.getAttributes(), toRemove);
      if (textualContent.isEmpty()) {
        textualContent.push("java");
      }
      val language = textualContent.pop();

      val codeNode =
          new NamedSyntaxNode<DocTree, Element>(
              language, ComponentElementParser.CodeElement, element, node);
      nodes.push(codeNode);
    }
    return null;
  }

  @Override
  public List<SyntaxNode<DocTree, Element>> visitEndElement(
      EndElementTree node, Set<DocTree> unused) {
    if (!nodes.isEmpty()) {
      val result = nodes.pop();
      unused.add(node);
      return Collections.singletonList(result);
    }
    return Collections.emptyList();
  }

  @Override
  public List<SyntaxNode<DocTree, Element>> visitUnknownBlockTag(
      UnknownBlockTagTree node, Set<DocTree> unused) {
    val result = new ArrayList<SyntaxNode<DocTree, Element>>();
    for (val c : node.getContent()) {
      val cr = super.visit(c, unused);
      if (cr != null) {
        result.addAll(cr);
      }
    }
    return result;
  }
}
