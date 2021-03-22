package com.aire.ux.docgen;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.sunshower.lambda.Exceptions;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import lombok.val;
import org.junit.jupiter.api.Test;

class AireDocletTest {

  @Test
  void ensureInvokingJavadocOnSampleSimpleClassWorks() throws URISyntaxException {

    val result = AireDocumentationManager.parse(new PrintWriter(System.out),
        loadFromClassPath("airedocs/airedocs/SampleSimpleClass.java"));

    assertNotNull(result);

  }

  private Collection<JavaFileObject> loadFromClassPath(String... paths) {
    return Arrays.stream(paths).map(ClassLoader::getSystemResource)
        .flatMap(t -> Exceptions.fromExceptional(t, URL::toURI).stream())
        .map(t -> Exceptions.fromExceptional(t, u -> new AireJavaFileObject(t, Kind.SOURCE)))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
