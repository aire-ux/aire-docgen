package com.aire.ux.docgen;

import com.aire.ux.docgen.model.DocumentationSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.tools.JavaFileObject;
import lombok.Getter;

public class DocumentationContext {

  @Getter
  private final Set<JavaFileObject> sources;
  @Getter
  private final DocumentationSet documentationSet;

  public DocumentationContext(Collection<? extends JavaFileObject> sources) {
    this.sources = new LinkedHashSet<>(sources);
    this.documentationSet = new DocumentationSet();
  }




}
