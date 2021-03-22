package com.aire.ux.docgen;

import com.aire.ux.docgen.model.ComponentParsingException;
import com.aire.ux.docgen.model.DocumentationSet;
import com.aire.ux.docgen.parsers.ComponentParser;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.util.ElementScanner9;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseAireElementWriter extends ElementScanner9<Void, Integer> {

  static final Logger log = LogManager.getLogger(BaseAireElementWriter.class);

  private final Reporter reporter;
  private final DocTrees documentForest;
  private final DocletEnvironment environment;

  private final Set<ComponentParser> parsers;

  public BaseAireElementWriter(
      DocTrees docTrees,
      DocletEnvironment environment,
      Reporter reporter) {
    this.reporter = reporter;
    this.environment = environment;
    this.documentForest = docTrees;
    this.parsers = new LinkedHashSet<>();
  }

  public void registerParser(@Nonnull ComponentParser parser) {
    this.parsers.add(parser);
  }

  public DocumentationSet process(Set<? extends Element> elements) {
    val documentationSet = new DocumentationSet();
    for (val element : elements) {
      val tree = documentForest.getDocCommentTree(element);
      if (tree != null) {
        if (parseDocumentation(documentationSet, tree)) {
          return null;
        }
      }
    }
    return documentationSet;
  }

  private boolean parseDocumentation(DocumentationSet documentationSet,
      DocCommentTree tree) {
    for (DocTree docTree : tree.getBlockTags()) {
      for (val parser : parsers) {
        if (parseDocumentationTree(documentationSet, docTree, parser)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean parseDocumentationTree(DocumentationSet documentationSet, DocTree docTree,
      ComponentParser parser) {
    if (parser.appliesTo(docTree)) {
      try {
        val descriptor = parser.parse(docTree, reporter);
        documentationSet.add(descriptor);
      } catch (ComponentParsingException ex) {
        return true;
      }
    }
    return false;
  }
}
