package com.aire.ux.docgen.parsers;


import com.aire.ux.docgen.ast.Symbol;

public record DefaultSymbol(String name) implements Symbol {
  @Override
  public String toString() {
    return name;
  }
}
