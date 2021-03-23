package com.aire.ux.docgen;

import com.aire.ux.docgen.ast.AbstractSyntaxTree;
import java.util.Locale;
import jdk.javadoc.doclet.Reporter;
import lombok.Getter;
import lombok.Setter;

public class ProcessingContext {

  @Getter
  final Locale locale;
  @Getter
  final Reporter reporter;

  @Getter
  @Setter
  private AbstractSyntaxTree syntaxTree;

  public ProcessingContext(Locale locale, Reporter reporter) {
    this.locale = locale;
    this.reporter = reporter;
  }
}
