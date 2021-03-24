package com.aire.ux.docgen.parsers;

import static java.lang.Character.isWhitespace;

import com.aire.ux.docgen.ast.NamedSyntaxNode;
import com.aire.ux.docgen.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.val;

@NotThreadSafe
@SuppressWarnings({
    "PMD.AssignmentInOperand",
    "PMD.CompareObjectsWithEquals",
    "PMD.UseEqualsToCompareStrings"
})
public class CodeBlockParser {

  public static final String ISLAND_PREFIX = "<code>";
  public static final String ISLAND_SUFFIX = "</code>";
  private final String content;
  private final DocTree source;
  private int end;
  private int start;
  private boolean parsed;

  public CodeBlockParser(@Nonnull String content) {
    this(content, null);
  }

  public CodeBlockParser(String content, final DocTree source) {
    this.end = 0;
    this.start = 0;
    this.parsed = false;
    this.source = source;
    this.content = content;
  }

  public boolean foundSections() {
    return parsed && start > 0;
  }


  public String getExtractedContent() {
    if (!parsed) {
      throw new IllegalStateException("Error: call parse() before calling this method");
    }

    if (!foundSections()) {
      return content;
    }

    return content
        .substring(0, getStart() - ISLAND_PREFIX.length())
        .concat(content.substring(getEnd() + ISLAND_SUFFIX.length()));
  }

  public List<SyntaxNode> parse() {
    int start = locate(ISLAND_PREFIX);
    if (start == -1) {
      this.start = 0;
      this.end = content.length();
      return Collections.emptyList();
    }

    this.end = start;
    this.start = start;
    val results = new ArrayList<SyntaxNode>();
    doParse(results);
    if (consumeUntil(e -> true, this.end, ISLAND_SUFFIX) == -1) {
      throw new ParsingException("Expected </code>--didn't get it (at %s)".formatted(this.end));
    }
    parsed = true;
    return results;
  }

  int getStart() {
    return start;
  }

  int getEnd() {
    return end;
  }
  private void doParse(ArrayList<SyntaxNode> results) {
    SyntaxNode node;
    while ((node = parseCodeBlock()) != null && end < content.length()) {
      results.add(node);
    }
  }

  private SyntaxNode parseCodeBlock() {
    this.end = chompWhitespace(this.end);
    val blockStart = consumeUntil(Character::isWhitespace, end, "```");
    if (blockStart == -1) {
      return null;
    }
    val name = takeUntil(blockStart, "\n");
    this.end = blockStart + name.length();
    val content = takeUntil(end, "```");
    this.end += content.length();
    this.end = chompWhitespace(this.end);
    this.end = consumeUntil(Character::isWhitespace, end, "```");
    return new NamedSyntaxNode(name, Symbols.Code, source, content, Collections.emptyList());
  }

  int chompWhitespace(int start) {
    int i;
    for (i = start; i < content.length() && isWhitespace(content.charAt(i)); i++) {
      ;
    }
    return i;
  }

  int consumeUntil(IntPredicate predicate, int start, String value) {
    int len = content.length();
    for (int i = start; i < len; ) {
      int j = 0;
      char ch;
      while (i < len && (ch = content.charAt(i++)) == value.charAt(j++)) {
        if (j == value.length()) {
          return i;
        }
        if (!predicate.test(ch) && ch != value.charAt(j)) {
          throw new ParsingException(
              "Unexpected content '%c' at location %d (expected '%c')"
                  .formatted(ch, i, value.charAt(j)));
        }
      }
    }
    return -1;
  }

  String takeUntil(int start, String pattern) {
    val result = new StringBuilder();
    int len = content.length();
    for (int i = start; i < len; ) {
      result.append(content.charAt(i));
      int j = 0;
      while (i < len && content.charAt(i++) == pattern.charAt(j++)) {
        if (j == pattern.length()) {
          return result.deleteCharAt(result.length() - 1).toString();
        }
      }
    }

    throw new ParsingException("Expected pattern '%s'--didn't get it".formatted(pattern));
  }

  int locate(String s) {
    return locate(0, s);
  }

  int locate(int startAt, String s) {
    int len = content.length();
    for (int i = startAt; i < len; ) {
      int j = 0;
      while (i < len && content.charAt(i++) == s.charAt(j++)) {
        if (j == s.length()) {
          return i;
        }
      }
    }
    return -1;
  }
}
