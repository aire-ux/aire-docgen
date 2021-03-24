package com.aire.ux.docgen;

import com.sun.source.doctree.DocTree;
import io.sunshower.lambda.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.lang.model.element.Element;
import lombok.Getter;
import lombok.val;

public class Context {

  @Getter final Element host;

  @Getter final List<? extends DocTree> tokens;

  @Getter final List<Parser> parsers;

  private final Map<String, Parser> parserCache;

  public Context(final List<Parser> parsers, final List<? extends DocTree> tokens) {
    this(null, parsers, tokens);
  }

  public Context(
      final Element element, final List<Parser> parsers, final List<? extends DocTree> tokens) {
    this.host = element;
    this.tokens = tokens;
    this.parsers = parsers;
    this.parserCache = new HashMap<>();
  }

  public Option<Parser> resolve(Function<DocTree, String> cacheKey, DocTree tree) {
    val key = cacheKey.apply(tree);
    val result = parserCache.get(key);

    if (result != null) {
      return Option.some(result);
    }

    for (val parser : parsers) {
      if (parser.appliesTo(tree)) {
        parserCache.put(key, parser);
        return Option.some(parser);
      }
    }

    return Option.none();
  }
}
