module aire.docgen.docgen.javadoc.main {
  requires jdk.compiler;
  requires static lombok;

  requires jdk.javadoc;
  requires org.apache.logging.log4j;
  requires static com.github.spotbugs.annotations;
}