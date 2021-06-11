package com.aire.ux.docgen;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.timeandspace.smoothie.SwissTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.ElementScanner9;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class DocumentationParser
    extends ElementScanner9<SyntaxNode<DocTree, Element>, SyntaxNode<DocTree, Element>> {

  static final Logger log = LogManager.getLogger(DocumentationParser.class);
  static final List<Parser> parsers;

  static {
    parsers = new ArrayList<>();
  }

  private final Reporter reporter;
  private final DocTrees documentForest;
  private final DocletEnvironment environment;
  private final Map<Element, Boolean> visited;
  private final Map<ElementKind, Parser> parserCache;
  private final Stack<SyntaxNode<DocTree, Element>> nodes;

  public DocumentationParser(DocTrees docTrees, DocletEnvironment environment, Reporter reporter) {
    this.reporter = reporter;
    this.environment = environment;
    this.documentForest = docTrees;

    this.nodes = new Stack<>();
    this.visited = new SwissTable<>();
    this.parserCache = new HashMap<>();
  }

  public static void register(Parser service) {
    log.info("Registering parser: {}", service);
    parsers.add(service);
    log.info("Successfully registered parser");
  }

  public AbstractSyntaxTree process(Set<? extends Element> elements) {
    val result = new AbstractSyntaxTree<DocTree, Element>();
    scan(elements, result.getRoot());
    return result;
  }

  @SuppressFBWarnings
  public SyntaxNode<DocTree, Element> scan(Element e, SyntaxNode<DocTree, Element> parent) {
    SyntaxNode<DocTree, Element> pnode = parent;
    if (!visited(e)) {
      val tree = documentForest.getDocCommentTree(e);
      if (e != null) {
        val node = new TagVisitor(parent, e).visit(tree, null);
        if (node != null) {
          pnode = node;
        }
      }
      visited.put(e, true);
    }

    return super.scan(e, pnode);
  }

  Parser lookupAndCache(Element tree, DocTree docTree) {
    for (val parser : parsers) {
      if (parser.appliesTo(tree, docTree)) {
        parserCache.put(tree.getKind(), parser);
        return parser;
      }
    }
    return null;
  }

  private boolean visited(Element e) {
    val result = visited.get(e);
    return result != null && result;
  }

  final class TagVisitor extends SimpleDocTreeVisitor<SyntaxNode<DocTree, Element>, Void> {

    final SyntaxNode node;
    final Element element;

    TagVisitor(@Nonnull final SyntaxNode<DocTree, Element> node, @Nonnull Element element) {
      this.node = node;
      this.element = element;
    }

    public SyntaxNode<DocTree, Element> visitDocComment(DocCommentTree tree, Void p) {
      return visit(tree.getBlockTags(), null);
    }

    public SyntaxNode<DocTree, Element> visitUnknownBlockTag(UnknownBlockTagTree tree, Void p) {
      val parser = lookupAndCache(element, tree);
      if (parser != null) {
        val blockSyntaxNode = parser.parse(element, tree);
        node.addChild(blockSyntaxNode);
        return blockSyntaxNode;
      }
      return super.visit(tree.getContent(), p);
    }
  }
}
