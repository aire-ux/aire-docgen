package com.aire.ux.docgen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.docgen.ast.NamedSyntaxNode;
import lombok.val;
import org.junit.jupiter.api.Test;

class CodeBlockParserTest {

  @Test
  void ensureParserCanLocateStartWithNoPrelude() {
    val content = """
        <code>
        </code>
        """;

    val parser = new CodeBlockParser(content);
    val end = parser.locate("<code>");
    val expected = content.indexOf(">") + 1;
    assertEquals(end, expected);
  }

  @Test
  void ensureParserCanLocateEndWithNoPostlude() {
    val content = """
        <code>
        </code>
        """;

    val parser = new CodeBlockParser(content);
    val end = parser.locate("</code>");
    val expected = content.lastIndexOf(">") + 1;
    assertEquals(end, expected);
  }

  @Test
  void ensureNoMatchResultsInNegativeReturnValue() {
    val parser = new CodeBlockParser("adfadfasdf");
    assertEquals(parser.locate("<hello>"), -1);
  }

  @Test
  void ensureParserCanLocateStringInRubbish() {
    val content =
        """
            adfasdgfasdgfasd
            fg
            asdfgasdfasdfa

            adfasdfasdf
            <code>

            adfadfasfasdfasdf

            asdfasdfasdfasf

            adfgadsgasdg
            """;

    val parser = new CodeBlockParser(content);

    val end = parser.locate("<code>");
    val expected = content.indexOf(">") + 1;
    assertEquals(end, expected);
  }

  @Test
  void ensureTakeUntilProducesPatternPrefix() {
    val expr =
        """
            hello


            world how are you?

            ?coolbeans
            <code>

            """;

    val result = new CodeBlockParser(expr).takeUntil(0, "<code>");

    val expected = """
        hello


        world how are you?

        ?coolbeans
        """;
    assertEquals(expected, result);
  }

  @Test
  void ensureCodeBlockParserCanParseWellFormedExpressionWithNoChildren() {

    val expression = """
        <code>
        </code>
        """;
    val result = new CodeBlockParser(expression).parse();
    assertTrue(result.isEmpty());
  }

  @Test
  void ensureCodeBlockParserCanParseWellFormedExpressionWithIslandPrelude() {
    val expression = """
        helloadfadf
        <code>
        </code>
        """;
    val result = new CodeBlockParser(expression).parse();
    assertTrue(result.isEmpty());
  }

  @Test
  void ensureCodeBlockParserCanParseWellFormedExpressionWithIslandPreludeWithWhitespace() {
    val expression = """
        helloadfadf
        <code>
                
                
                
        </code>
        """;
    val result = new CodeBlockParser(expression).parse();
    assertTrue(result.isEmpty());
  }

  @Test
  void ensureCodeBlockParseCanParseWellFormedExpressionWithSingleChildAndNoPrelude() {
    val content = """
        def hello() {
        }
        """.strip();
    val expr =
        """
            <code>
            ```groovy
            %s
            ```
            </code>
            """
            .formatted(content).strip();

    val result = new CodeBlockParser(expr).parse();
    assertEquals(1, result.size());

    val extracted = result.get(0);
    assertTrue(extracted instanceof NamedSyntaxNode);
    assertEquals("groovy", ((NamedSyntaxNode) extracted).getName());
    assertEquals(content, extracted.getContent().trim());
  }


  @Test
  void ensureFullValuesWork() {
    val content = """
        def hello() {
        }
        """.strip();
    val expr =
        """
            aadsfasdfadfafasdfadf
                        
                        
            hello world
            <code>
            ```groovy
            %s
            ```
            ```java
             private void doParse(ArrayList<SyntaxNode> results) {
               SyntaxNode node;
               while ((node = parseCodeBlock()) != null && end < content.length()) {
                 results.add(node);
               }
             }
                       
                       
             private SyntaxNode parseCodeBlock() {
               this.end = chompWhitespace(this.end);
               val blockStart = consumeUntil(Character::isWhitespace, end, "`");
               if (blockStart == -1) {
                 return null;
               }
               val name = takeUntil(blockStart, "\\n");
               this.end = blockStart + name.length();
               val content = takeUntil(end, "");
               this.end += content.length();
               this.end = chompWhitespace(this.end);
               this.end = consumeUntil(Character::isWhitespace, end, "`");
               return new NamedSyntaxNode(name, Symbols.Code, source, content, Collections.emptyList());
             }
                        
            ```
            </code>
                        
                        
                        basdfafadfg
            adfadgasdgasdgasdgf
            """
            .formatted(content).strip();

    val parser = new CodeBlockParser(expr);
    val result = parser.parse();
    assertEquals(2, result.size());

    val extracted = result.get(0);
    assertTrue(extracted instanceof NamedSyntaxNode);
    assertEquals("groovy", ((NamedSyntaxNode) extracted).getName());
    assertEquals(content, extracted.getContent().trim());

    System.out.println(expr.substring(0, parser.getStart() - "<code>".length())
        .concat(expr.substring(parser.getEnd() + "</code>".length()
        )));
  }

}
