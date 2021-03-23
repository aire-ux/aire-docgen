package com.aire.ux.docgen;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Stack;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AireDocumentationManager {

  static final Logger log = LogManager.getLogger(AireDocumentationManager.class);

  private static final ThreadLocal<ProcessingContext> processingContext;
  private static final ThreadLocal<Stack<ProcessingContext>> pendingContexts;

  static {
    pendingContexts = new ThreadLocal<>();
    pendingContexts.set(new Stack<>());
    processingContext = new ThreadLocal<>();
  }

  public static ProcessingContext parse(final Collection<JavaFileObject> paths) {
    return parse(new PrintWriter(System.out), paths);
  }

  public static ProcessingContext parse(
      final Writer writer, final Collection<JavaFileObject> paths) {
    reload();
    AireDoclet.setFiles(paths);
    val tool =
        ToolProvider.getSystemDocumentationTool()
            .getTask(writer, null, null, AireDoclet.class, null, paths);
    tool.call();
    val current = processingContext.get();
    if (current == null) {
      throw new IllegalStateException("Error: documentation context didn't exist for some reason");
    }
    AireDoclet.clearFiles();
    return current;
  }

  static void popContext(ProcessingContext context) {
    val ctx = pendingContexts.get().pop();
    if (!Objects.equals(ctx, context)) {
      throw new AireDocumentationException(
          "Error (probably concurrency-related): documentation context '%s' is not the expected one '%s'"
              .formatted(ctx, context));
    }
    processingContext.set(ctx);
  }

  static void pushContext(ProcessingContext processingContext) {
    pendingContexts.get().push(processingContext);
  }

  private static void reload() {
    log.info("Loading component parsers...");
    val serviceLoader = ServiceLoader.load(Parser.class);
    for (val service : serviceLoader) {
      DocumentationParser.register(service);
    }
    log.info("Successfully loaded component parsers");
  }
}
