package com.aire.ux.docgen.emitters;

import static java.lang.String.format;

import com.aire.ux.docgen.parsers.ComponentElementParser;
import com.aire.ux.docgen.parsers.GroupParser;
import com.aire.ux.docgen.parsers.PropertyParser;
import com.aire.ux.parsers.ast.AbstractSyntaxTree;
import com.aire.ux.parsers.ast.NamedSyntaxNode;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.sun.source.doctree.DocTree;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.element.Element;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

public class YamlEmitter implements Emitter {

  static final String FORMAT = "yaml";

  @Override
  public boolean appliesTo(String fmt) {
    return FORMAT.equalsIgnoreCase(fmt);
  }

  @Override
  public void emit(AbstractSyntaxTree<DocTree, Element> tree, File output) {
    try (val writer = new BufferedWriter(new FileWriter(output))) {
      doWrite(writer, tree);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private void doWrite(BufferedWriter writer, AbstractSyntaxTree<DocTree, Element> tree)
      throws IOException {
    val root = tree.getRoot();
    val result = new HashMap<String, Object>();
    val components = new ArrayList<Map<String, Object>>();
    for (val child : root.getChildren()) {
      if (Objects.equals(ComponentElementParser.ComponentElement, child.getSymbol())) {
        writeComponent(null, (NamedSyntaxNode<DocTree, Element>) child, components);
      }
    }
    result.put("components", components);
    new Yaml().dump(result, writer);
  }

  private void writeComponent(String parentName, NamedSyntaxNode<DocTree, Element> child,
      List<Map<String, Object>> result) {
    val componentMap = writeComponentDefinition(parentName, child);
    writeGroupIfAny(child, componentMap);
    result.add(componentMap);

    for (val c : child.getChildren()) {
      if (Objects.equals(ComponentElementParser.ComponentElement, c.getSymbol())) {
        writeComponent((String) componentMap.get("component-name"),
            (NamedSyntaxNode<DocTree, Element>) c, result);
      }
    }

  }

  private Map<String, Object> writeComponentDefinition(String parentName,
      NamedSyntaxNode<DocTree, Element> child) {
    val component = new HashMap<String, Object>();
    if (parentName != null) {
      component.put("component-name", format("%s.%s", parentName, child.getName()));
    } else {
      component.put("component-name", child.getName());
    }

    val pkg = child.getProperty("package");
    if (pkg != null) {
      component.put("package", pkg);
    }

    val properties = new ArrayList<Map<String, Object>>();
    writeProperties(child, properties);
    component.put("properties", properties);

    val codes = new ArrayList<Map<String, Object>>();
    writeCodes(child, codes);
    component.put("examples", codes);
    component.put("description", child.getContent());
    return component;
  }

  private void writeCodes(NamedSyntaxNode<DocTree, Element> child,
      ArrayList<Map<String, Object>> codes) {

    for (val c : child.getChildren()) {
      if (Objects.equals(c.getSymbol(), ComponentElementParser.CodeElement)) {
        writeCode((NamedSyntaxNode<DocTree, Element>) c, codes);
      }
    }
  }

  private void writeCode(NamedSyntaxNode<DocTree, Element> c,
      ArrayList<Map<String, Object>> codes) {

    val code = new HashMap<String, Object>();
    code.put("lang", c.getName());
    code.put("value", c.getContent());
    codes.add(code);
  }

  private void writeProperties(NamedSyntaxNode<DocTree, Element> child,
      List<Map<String, Object>> properties) {

    for (val c : child.getChildren()) {
      if (Objects.equals(c.getSymbol(), PropertyParser.PropertySymbol)) {
        val propertyDefinition = new LinkedHashMap<String, Object>();
        writePropertyDefinition((NamedSyntaxNode<DocTree, Element>) c, propertyDefinition);
        properties.add(propertyDefinition);
      }
    }
  }

  private void writePropertyDefinition(NamedSyntaxNode<DocTree, Element> c,
      Map<String, Object> propertyDefinition) {
    propertyDefinition.put("name", c.getName());
    propertyDefinition.put("description", c.getContent());
    propertyDefinition.put("type", c.getProperty("type"));
    val codes = new ArrayList<Map<String, Object>>();
    writeCodes(c, codes);
    propertyDefinition.put("examples", codes);
  }

  private void writeGroupIfAny(SyntaxNode<DocTree, Element> child, Map<String, Object> result) {
    val group = getGroup(child);
    if (group != null) {
      result.put("group", group);
    }
  }

  private String getGroup(SyntaxNode<DocTree, Element> child) {
    for (val c : child.getChildren()) {
      if (Objects.equals(c.getSymbol(), GroupParser.GroupSymbol)) {
        return ((NamedSyntaxNode<DocTree, Element>) c).getName();
      }
    }
    return null;
  }
}
