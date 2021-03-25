package com.aire.ux.docgen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.SimpleJavaFileObject;
import lombok.val;

public class AireJavaFileObject extends SimpleJavaFileObject {

  final CharSequence contents;

  public AireJavaFileObject(URI uri, Kind kind) throws IOException {
    super(uri, kind);
    try (val stream = uri.toURL().openStream()) {
      contents = read(stream);
    }
  }

  /**
   * Construct a SimpleJavaFileObject of the given kind and with the given URI.
   *
   * @param uri the URI for this file object
   * @param kind the kind of this file object
   */
  public AireJavaFileObject(URI uri, CharSequence contents, Kind kind) {
    super(uri, kind);
    this.contents = contents;
  }

  public AireJavaFileObject(URI uri, CharSequence contents) {
    this(uri, contents, Kind.SOURCE);
  }

  public AireJavaFileObject(CharSequence contents) {
    this(URI.create("mem://source/mem/java"), contents, Kind.SOURCE);
  }

  /**
   * @param file the file to load
   * @param kind the kind that is backed by the path
   * @throws IOException if reading the path occurs
   */
  public AireJavaFileObject(File file, Kind kind) throws IOException {
    this(file.toURI(), file.toPath(), kind);
  }

  /**
   * @param path the path to load
   * @param kind the kind that is backed by the path
   * @throws IOException if reading the path occurs
   */
  public AireJavaFileObject(URI uri, Path path, Kind kind) throws IOException {
    this(uri, Files.readString(path), kind);
  }

  @SuppressWarnings("PMD.AssignmentInOperand")
  private static CharSequence read(InputStream inputStream) throws IOException {
    val buffer = new ByteArrayOutputStream();
    int nRead;
    val data = new byte[1024];
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    buffer.flush();
    return buffer.toString(StandardCharsets.UTF_8);
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    return contents;
  }
}
