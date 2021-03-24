package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;
import io.timeandspace.smoothie.SwissTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.ElementScanner9;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocumentationParser extends ElementScanner9<SyntaxNode, SyntaxNode> {

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

  public DocumentationParser(DocTrees docTrees, DocletEnvironment environment, Reporter reporter) {
    this.reporter = reporter;
    this.environment = environment;
    this.documentForest = docTrees;
    this.parserCache = new HashMap<>();
    this.visited = new SwissTable<>();
  }

  public static void register(Parser service) {
    log.info("Registering parser: {}", service);
    parsers.add(service);
    log.info("Successfully registered parser");
  }


  public AbstractSyntaxTree process(Set<? extends Element> elements) {
    val result = new AbstractSyntaxTree();
    scan(elements, result.getRoot());
    return result;
  }


  public SyntaxNode scan(Element e, SyntaxNode parent) {
    if (!visited(e)) {
      val tree = documentForest.getDocCommentTree(e);
      if (e != null) {
        new TagVisitor(parent, e).visit(tree, null);
      }
      visited.put(e, true);
    }

    return super.scan(e, parent);
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

  final class TagVisitor extends SimpleDocTreeVisitor<Void, Void> {

    final SyntaxNode node;
    final Element element;

    TagVisitor(@Nonnull final SyntaxNode node, @Nonnull Element element) {
      this.node = node;
      this.element = element;
    }

    public Void visitDocComment(DocCommentTree tree, Void p) {
      return visit(tree.getBlockTags(), null);
    }


    public Void visitUnknownBlockTag(UnknownBlockTagTree tree, Void p) {
      val parser = lookupAndCache(element, tree);
      if (parser != null) {
        val blockSyntaxNode = parser.parse(element, tree);
        node.addChild(blockSyntaxNode);
      }
      super.visit(tree.getContent(), p);

      checkClosures();
      return null;
    }


    private void checkClosures() {

    }

  }
}
