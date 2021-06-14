package com.aire.ux.docgen.emitters;

import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.sun.source.doctree.DocTree;
import java.io.File;
import javax.lang.model.element.Element;

public interface Emitter {

  boolean appliesTo(String fmt);

  void emit(AbstractSyntaxTree<DocTree, Element> tree, File output);

}
