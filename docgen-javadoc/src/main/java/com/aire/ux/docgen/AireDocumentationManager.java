package com.aire.ux.docgen;

import static java.lang.String.format;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.annotation.concurrent.NotThreadSafe;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@NotThreadSafe
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
    return parse(new PrintWriter(System.out, true, StandardCharsets.UTF_8), paths);
  }

  public static ProcessingContext parse(
      final Writer writer, final Collection<JavaFileObject> paths) {
    reload();
    AireDoclet.setFiles(paths);
    val tool =
        ToolProvider.getSystemDocumentationTool()
            .getTask(
                writer, null, null, AireDoclet.class, List.of("--show-members", "private"), paths);
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
          format(
              "Error (probably concurrency-related): documentation context '%s' is not the expected one '%s'",
              ctx, context));
    }
    processingContext.set(ctx);
  }

  static void pushContext(ProcessingContext processingContext) {
    pendingContexts.get().push(processingContext);
  }

  static void reload() {
    log.info("Loading component parsers...");
    val serviceLoader =
        ServiceLoader.load(Parser.class, Thread.currentThread().getContextClassLoader());
    for (val service : serviceLoader) {
      DocumentationParser.register(service);
    }
    log.info("Successfully loaded component parsers");
  }

  public static ProcessingContext parse(Writer writer, String... contents) {
    Collection<JavaFileObject> fileObjects =
        Arrays.stream(contents).map(AireJavaFileObject::new).collect(Collectors.toList());
    return parse(writer, fileObjects);
  }

  @SuppressFBWarnings
  public static ProcessingContext parse(String... contents) {
    return parse(new PrintWriter(System.out), contents);
  }

  @SuppressFBWarnings
  public static ProcessingContext parse(URI uri, String contents) {
    val obj = new AireJavaFileObject(uri, contents);
    return parse(new PrintWriter(System.out), Collections.singletonList(obj));
  }
}
