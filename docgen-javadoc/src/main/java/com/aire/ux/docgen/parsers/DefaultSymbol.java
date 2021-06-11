package com.aire.ux.docgen.parsers;

import com.aire.ux.parsers.ast.Symbol;
import lombok.Data;

@Data
public class DefaultSymbol implements Symbol {

  final String name;

  @Override
  public String toString() {
    return name;
  }
}
