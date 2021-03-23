package com.aire.ux.docgen;

import io.sunshower.lambda.Exceptions;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class DocletTests {

  public static Collection<JavaFileObject> loadFromClassPath(String... paths) {
    return Arrays.stream(paths).map(ClassLoader::getSystemResource)
        .flatMap(t -> Exceptions.fromExceptional(t, URL::toURI).stream())
        .map(t -> Exceptions.fromExceptional(t, u -> new AireJavaFileObject(t, Kind.SOURCE)))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
