# Aire-Docgen

Aire-Docgen is a documentation generator for Vaadin widgets.  The goals for this project are as follows:

1. It should be internationalizable/localizable: documentation should be easy to generate for any language
1. It should support running/displaying snippets
1. It should facilitate literate programming (i.e. the documentation should be embeddable within the code) 

# Overview

Using Aire-Docgen should be straightforward.  Simply add the Gradle (tk: docs), or Maven (tk: docs) plugins to your
build, optionally configure the source directories they should scan (default is `src/main/java`), optionally
configure the output directory (default is `<target>/aire-docs/`)

# Structure

```java
package io.coolwidgets;


/**
 * @documented
 */
public class MyComponent {
  
}


```