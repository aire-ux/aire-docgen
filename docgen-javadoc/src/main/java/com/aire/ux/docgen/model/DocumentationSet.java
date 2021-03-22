package com.aire.ux.docgen.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DocumentationSet implements Descriptor {


  private final Map<Class<?>, Set<Descriptor>> descriptors;

  public DocumentationSet() {
    descriptors = new LinkedHashMap<>();
  }

  public void add(Descriptor descriptor) {
    descriptors.computeIfAbsent(descriptor.getClass(), e -> new LinkedHashSet<>()).add(descriptor);
  }
}
