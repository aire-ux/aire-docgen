package com.aire.ux.docgen;

import static java.lang.String.format;

import com.aire.ux.docgen.ast.SyntaxTreeRewriteRule;
import com.aire.ux.docgen.emitters.Emitter;
import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.sun.source.doctree.DocTree;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AireDoclet implements Doclet {

  static final Logger log = LogManager.getLogger(AireDoclet.class);
  static final Object filesLock = new Object();
  static final String ANY_FORMAT = "any";
  private static final ThreadLocal<Collection<JavaFileObject>> files;

  static {
    files = new ThreadLocal<>();
    AireDocumentationManager.reload();
  }

  private String format;
  private Locale locale;
  private Reporter reporter;
  private File outputDirectory;
  private ProcessingContext context;

  public AireDoclet() {
    super();
  }

  public static void setFiles(Collection<JavaFileObject> files) {
    synchronized (filesLock) {
      checkForCurrent();
      AireDoclet.files.set(files);
    }
  }

  public static Collection<JavaFileObject> clearFiles() {
    synchronized (filesLock) {
      val existing = AireDoclet.files.get();
      if (existing == null) {
        throw new IllegalStateException("Error: there is no pending extraction operation");
      }
      AireDoclet.files.set(null);
      return existing;
    }
  }

  private static void checkForCurrent() {
    val existing = AireDoclet.files.get();
    if (existing != null) {
      throw new IllegalStateException("Error: there is an existing, pending extraction operation");
    }
  }

  @Override
  public void init(Locale locale, Reporter reporter) {
    log.log(
        Level.INFO, "Initializing AireDoclet with (locale: {}, reporter: {})", locale, reporter);

    this.locale = locale;
    this.reporter = reporter;
    AireDocumentationManager.pushContext(context = createContext());
    log.info("Completed initializing AireDoclet");
  }

  private ProcessingContext createContext() {
    return new ProcessingContext(locale, reporter);
  }

  @Override
  public String getName() {
    return AireDoclet.class.getSimpleName();
  }

  @Override
  public Set<? extends Doclet.Option> getSupportedOptions() {
    return Set.of(
        new Option("-d", true, "output parsed documentation to this directory", null) {
          @Override
          public boolean process(String option, List<String> arguments) {
            if (arguments == null || arguments.isEmpty()) {
              log.warn(
                  "No output directory specified, defaulting to process current directory ({})",
                  System.getProperty("user.dir"));
              AireDoclet.this.setOutputDirectory(System.getProperty("user.dir"));
              return true;
            }
            AireDoclet.this.setOutputDirectory(arguments.get(0));
            return true;
          }
        },
        new Option("--format", true, "write documentation in this format", null) {
          @Override
          public boolean process(String option, List<String> arguments) {
            if (arguments == null || arguments.isEmpty()) {
              log.info("No format specified.  Writing to first available formatter");
              AireDoclet.this.setFormat("any");
              return true;
            }
            AireDoclet.this.setFormat(arguments.get(0));
            return true;
          }
        }
    );
  }

  private void setOutputDirectory(String s) {
    log.info("Using output directory '{}'", s);
    this.outputDirectory = validate(s);
  }


  private void setFormat(String format) {
    log.info("Requested format '{}'", format);
    this.format = format;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    log.info("Running AireDoclet with DocletEnvironment: {}", environment);
    val docTrees = environment.getDocTrees();
    val parser = new DocumentationParser(docTrees, environment, reporter);
    val elements = environment.getIncludedElements();
    val syntaxTree = rewrite(parser.process(elements));
    context.setSyntaxTree(syntaxTree);
    AireDocumentationManager.popContext(context);
    write(syntaxTree);
    return true;
  }

  private void write(AbstractSyntaxTree<DocTree, Element> rewrite) {
    if(format == null) {
      log.info("No format requested, not emitting any documentation");
      return;
    }
    val emitter = lookupEmitter();
    val localeDirectory = new File(outputDirectory, locale.toString());
    if (!(localeDirectory.exists() || localeDirectory.mkdirs())) {
      throw new IllegalStateException(
          format("Error: failed to create directory '%s' and it does not exist", localeDirectory));
    }
    emitter.emit(rewrite, new File(localeDirectory, format("aire.%s", format)));
  }

  private Emitter lookupEmitter() {
    for (val emitter : ServiceLoader.load(Emitter.class, Thread.currentThread()
        .getContextClassLoader())) {
      if (format == null || ANY_FORMAT.equalsIgnoreCase(format)) {
        return emitter;
      }

      if (emitter.appliesTo(format)) {
        log.info("Located emitter '{}' for format '{}'", emitter, format);
        return emitter;
      }
    }
    throw new NoSuchElementException(format("No emitter for format '%s'", format));
  }

  @SuppressWarnings("unchecked")
  private AbstractSyntaxTree<DocTree, Element> rewrite(AbstractSyntaxTree<DocTree, Element> tree) {
    for (val rewriteRule :
        ServiceLoader
            .load(SyntaxTreeRewriteRule.class, Thread.currentThread().getContextClassLoader())) {
      tree = rewriteRule.rewrite(tree);
    }
    return tree;
  }

  private File validate(String s) {
    val file = new File(s);
    if (file.exists() && !file.isDirectory()) {
      throw new IllegalArgumentException(
          format("File '%s' exists, but is not a directory", file.getAbsolutePath()));
    }

    if (!file.exists()) {
      log.info("Directory ''{}'' does not exist--attempting to create it", file);
      if (!file.mkdirs()) {
        throw new IllegalStateException(
            format(
                "Error: could not create directory '%s' and it does not exist.  Not continuing",
                file));
      }
    }
    return file;
  }

  abstract static class Option implements Doclet.Option {

    private final String name;
    private final boolean hasArg;
    private final String description;
    private final String parameters;

    Option(String name, boolean hasArg, String description, String parameters) {
      this.name = name;
      this.hasArg = hasArg;
      this.description = description;
      this.parameters = parameters;
    }

    @Override
    public int getArgumentCount() {
      return hasArg ? 1 : 0;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public Kind getKind() {
      return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
      return List.of(name);
    }

    @Override
    public String getParameters() {
      return hasArg ? parameters : "";
    }
  }
}
