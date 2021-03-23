package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.util.DocTrees;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.util.ElementScanner9;
import javax.tools.JavaFileObject;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocumentationParser extends ElementScanner9<Void, Integer> {

  static final Logger log = LogManager.getLogger(DocumentationParser.class);
  static final List<Parser> parsers;

  static {
    parsers = new ArrayList<>();
  }

  private final Reporter reporter;
  private final DocTrees documentForest;
  private final DocletEnvironment environment;
  private final Map<Kind, Parser> parserCache;

  public DocumentationParser(
      DocTrees docTrees,
      DocletEnvironment environment,
      Reporter reporter) {
    this.reporter = reporter;
    this.environment = environment;
    this.documentForest = docTrees;
    this.parserCache = new HashMap<>();
  }

  public static void register(Parser service) {
    log.info("Registering parser: {}", service);
    parsers.add(service);
    log.info("Successfully registered parser");
  }

  public AbstractSyntaxTree process(Set<? extends Element> elements) {
    for (val element : elements) {
      val commentTree = documentForest.getDocCommentTree(element);
      if (commentTree != null) {
        val parser = lookupAndCache(commentTree);
        if (parser != null) {
          val context = new Context(element, commentTree.getBlockTags());
          return parser.parse(context);
        }
      }
    }
    return null;
  }

  private Parser lookupAndCache(DocTree tree) {
    val existing = parserCache.get(tree.getKind());
    if (existing != null) {
      return existing;
    }
    for (val parser : parsers) {
      if (parser.appliesTo(tree)) {
        parserCache.put(tree.getKind(), parser);
        return parser;
      }
    }
    return null;
  }

}
