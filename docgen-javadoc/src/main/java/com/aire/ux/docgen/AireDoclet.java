package com.aire.ux.docgen;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AireDoclet implements Doclet {

  static final Logger log = LogManager.getLogger(AireDoclet.class);
  static final Object filesLock = new Object();
  private static final ThreadLocal<Collection<JavaFileObject>> files;

  static {
    files = new ThreadLocal<>();
  }

  private Locale locale;
  private Reporter reporter;
  private DocumentationContext context;

  public AireDoclet() {

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
    log.info("Initializing AireDoclet with (locale: {}, reporter: {})", locale, reporter);
    this.locale = locale;
    this.reporter = reporter;
    AireDocumentationManager.pushContext(context = createContext());
    log.info("Completed initializing AireDoclet");
  }

  private DocumentationContext createContext() {
    return new DocumentationContext(files.get());
  }

  @Override
  public String getName() {
    return AireDoclet.class.getSimpleName();
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return Collections.emptySet();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    log.info("Running AireDoclet with DocletEnvironment: {}", environment);
    val docTrees = environment.getDocTrees();
    val writer = new BaseAireElementWriter(docTrees, environment, reporter);
    val elements = environment.getIncludedElements();
    writer.process(elements);
    AireDocumentationManager.popContext(context);
    return true;
  }
}
