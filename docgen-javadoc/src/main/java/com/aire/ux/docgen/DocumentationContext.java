package com.aire.ux.docgen;

import com.aire.ux.docgen.model.DocumentationSet;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class DocumentationContext {

  private final Set<Path> paths;
  private final DocumentationSet documentationSet;

  public DocumentationContext() {
    paths = new LinkedHashSet<>();
    documentationSet = new DocumentationSet();
  }

}
