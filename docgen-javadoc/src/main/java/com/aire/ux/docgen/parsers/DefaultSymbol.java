package com.aire.ux.docgen.parsers;

import com.aire.ux.parsers.ast.Symbol;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public record DefaultSymbol(String name) implements Symbol {
  @Override
  public String toString() {
    return name;
  }
}
