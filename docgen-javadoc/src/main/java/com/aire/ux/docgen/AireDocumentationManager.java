package com.aire.ux.docgen;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Stack;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import lombok.val;

public class AireDocumentationManager {


  private static final ThreadLocal<DocumentationContext> documentationContext;
  private static final ThreadLocal<Stack<DocumentationContext>> pendingContexts;


  static {
    pendingContexts = new ThreadLocal<>();
    pendingContexts.set(new Stack<>());
    documentationContext = new ThreadLocal<>();
  }


  public static DocumentationContext parse(final Writer writer,
      final Collection<JavaFileObject> paths) {

    val tool =
        ToolProvider.getSystemDocumentationTool()
            .getTask(
                new PrintWriter(System.out),
                null,
                null,
                AireDoclet.class,
                null,
                paths);
    tool.call();
    val current = documentationContext.get();
    if (current == null) {
      throw new IllegalStateException("Error: documentation context didn't exist for some reason");
    }
    return current;
  }


  static void popContext(DocumentationContext context) {
    val ctx = pendingContexts.get().pop();
    if (!Objects.equals(ctx, context)) {
      throw new AireDocumentationException(
          "Error (probably concurrency-related): documentation context '%s' is not the expected one '%s'"
              .formatted(ctx, context));
    }
    documentationContext.set(ctx);
  }

  static void pushContext(DocumentationContext documentationContext) {
    pendingContexts.get().push(documentationContext);
  }
}
