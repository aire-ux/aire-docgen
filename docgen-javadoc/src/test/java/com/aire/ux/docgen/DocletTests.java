package com.aire.ux.docgen;

import io.sunshower.lambda.Exceptions;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import lombok.val;

public class DocletTests {

  public static Collection<JavaFileObject> loadFromClassPath(String... paths) {
    return Arrays.stream(paths)
        .map(ClassLoader::getSystemResource)
        .flatMap(t -> Exceptions.fromExceptional(t, URL::toURI).stream())
        .map(t -> Exceptions.fromExceptional(t, u -> new AireJavaFileObject(t, Kind.SOURCE)))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  public static String testOutput() {
    val cwd = new File(
        Objects.requireNonNull(DocletTests.class.getClassLoader().getResource(".")).getFile());
    for (var c = cwd; c != null; c = c.getParentFile()) {
      val buildDir = new File(c, "build");
      if (buildDir.exists() && buildDir.isDirectory()) {
        return buildDir.getAbsolutePath();
      }
    }
    throw new IllegalStateException("No build directory somehow");
  }

  public static Set<JavaFileObject> hierarchyIn(String s) {
    val parent = new File(testOutput(), s);
    val results = new LinkedHashSet<JavaFileObject>();
    val stack = new ArrayDeque<File>();
    stack.push(parent);

    while (!stack.isEmpty()) {
      val current = stack.pop();
      for (val file : Objects
          .requireNonNull(current.listFiles(t -> t.isFile() && t.getName().endsWith(".java")))) {
        try {
          results.add(new AireJavaFileObject(file, Kind.SOURCE));
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }

      for (val childDir : Objects.requireNonNull(current.listFiles(File::isDirectory))) {
        stack.push(childDir);
      }
    }
    return results;
  }
}
