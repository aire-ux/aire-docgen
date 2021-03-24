package com.aire.ux.docgen.parsers;


import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
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

public class CodeNodeVisitor extends SimpleDocTreeVisitor<List<SyntaxNode>, Set<DocTree>> {

  private final Element element;
  private final Stack<SyntaxNode> nodes;
  private final Stack<String> textualContent;

  CodeNodeVisitor(final Element element) {
    this.element = element;
    this.nodes = new Stack<>();
    this.textualContent = new Stack<>();
  }

  @Override
  public List<SyntaxNode> visitText(TextTree node, Set<DocTree> unused) {
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
  public List<SyntaxNode> visitAttribute(AttributeTree node, Set<DocTree> docTrees) {
    val name = node.getName().toString();
    if ("lang".equals(name)) {
      textualContent.push(node.getValue().get(0).toString());
    }
    return Collections.emptyList();
  }

  @Override
  public List<SyntaxNode> visitStartElement(StartElementTree node, Set<DocTree> toRemove) {
    val name = node.getName().toString();
    if ("code".equals(name)) {
      toRemove.add(node);
      this.visit(node.getAttributes(), toRemove);
      if (textualContent.isEmpty()) {
        textualContent.push("java");
      }
      val language = textualContent.pop();

      val codeNode = new NamedSyntaxNode(
          language,
          ComponentElementParser.CodeElement,
          element,
          node
      );
      nodes.push(codeNode);
    }
    return null;
  }

  @Override
  public List<SyntaxNode> visitEndElement(EndElementTree node, Set<DocTree> unused) {
    if (!nodes.isEmpty()) {
      val result = nodes.pop();
      unused.add(node);
      return Collections.singletonList(result);
    }
    return Collections.emptyList();
  }

  @Override
  public List<SyntaxNode> visitUnknownBlockTag(UnknownBlockTagTree node, Set<DocTree> unused) {
    val result = new ArrayList<SyntaxNode>();
    for(val c : node.getContent()) {
      val cr = super.visit(c, unused);
      if(cr != null) {
        result.addAll(cr);
      }
    }
    return result;
  }
}
