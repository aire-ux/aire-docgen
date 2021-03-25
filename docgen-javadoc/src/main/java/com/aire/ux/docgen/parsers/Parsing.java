package com.aire.ux.docgen.parsers;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import lombok.val;

public class Parsing {

  public static String extractTextNodes(Iterable<? extends DocTree> tree) {
    val result = new StringBuilder();
    for (val node : tree) {
      if (node.getKind() == Kind.TEXT) {
        result.append(node.toString());
      }
    }
    return result.toString();
  }
}
