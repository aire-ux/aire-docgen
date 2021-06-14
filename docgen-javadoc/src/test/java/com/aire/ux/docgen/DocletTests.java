package com.aire.ux.docgen;

import io.sunshower.lambda.Exceptions;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
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
    for(var c = cwd; c != null; c = c.getParentFile()) {
      val buildDir = new File(c, "build");
      if(buildDir.exists() && buildDir.isDirectory()) {
        return c.getAbsolutePath();
      }
    }
    throw new IllegalStateException("No build directory somehow");
  }
}
