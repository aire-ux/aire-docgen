package com.aire.ux.docgen;

import com.sun.source.doctree.DocTree;
import java.net.URI;
import java.util.List;
import javax.lang.model.element.Element;
import lombok.Getter;

public class Context {

  @Getter
  final Element host;

  @Getter
  final List<? extends DocTree> tokens;

  public Context(final Element element,
      final List<? extends DocTree> tokens) {
    this.tokens = tokens;
    this.host = element;
  }


}
