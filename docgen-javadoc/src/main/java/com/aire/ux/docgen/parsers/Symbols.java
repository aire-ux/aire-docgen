package com.aire.ux.docgen.parsers;

import com.aire.ux.docgen.ast.Symbol;
import lombok.Getter;

public enum Symbols implements Symbol {
  Code("code"),
  Name("name"),
  Article("article"),
  Component("component");

  @Getter final String value;

  Symbols(String value) {
    this.value = value;
  }
}
