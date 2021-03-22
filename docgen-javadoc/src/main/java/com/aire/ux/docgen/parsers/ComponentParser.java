package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.model.Descriptor;
import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Reporter;

public interface ComponentParser {

  boolean appliesTo(DocTree tree);

  Class<?> getDescriptorType();

  Descriptor parse(DocTree docTree, Reporter reporter);
}
