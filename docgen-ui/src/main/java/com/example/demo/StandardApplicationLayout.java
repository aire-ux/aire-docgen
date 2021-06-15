package com.example.demo;

import com.example.demo.Elements.Attributes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Nav;
import javax.annotation.Nonnull;
import lombok.val;

@Tag("standard-application-layout")
@JsModule("./src/layouts/standard-application-layout.ts")
@NpmPackage(value = "lit-element", version = "2.5.1")
public class StandardApplicationLayout extends HtmlContainer {

  public enum Mode {


    Overlay,


    Push
  }


  /**
   * slot-name for header
   */
  static final String HEADER = "header";

  /**
   * slot name for navigation
   */
  static final String NAVIGATION = "navigation";

  public enum Slot {
    /**
     * header slot
     */
    Header(HEADER, HEADER),

    /**
     * navigation slot
     */
    Navigation(NAVIGATION, NAVIGATION);

    final String name;
    final String[] additionalClasses;
    Slot(String name, String...additionalClasses) {
      this.name = name;
      this.additionalClasses = additionalClasses;
    }

    public Component set(Component component) {
      Attributes.Slot.set(component, name);
      for(val className : additionalClasses) {
        Elements.addClass(component, className);
      }
      return component;
    }
  }

  private final HasComponents header;

  private final HasComponents navigation;



  public StandardApplicationLayout() {

    /**
     * set up and configure header
     */
    header = createHeader();
    super.add((Component) header);

    /**
     * set up and configure navigation
     */
    navigation = createNavigation();
    super.add((Component) navigation);


  }


  public void add(Slot slot, Component component) {
    switch (slot) {
      case Header:
        addToHeader(component);
        return;
    }
  }

  public void addToHeader(@Nonnull Component... components) {
    header.add(components);
  }


  /**
   * create a header component
   * @param <T> the type-parameter of the type of the header element
   * @return the header element (should call Slot.Header.set(result)
   */

  @SuppressWarnings("unchecked")
  protected <T extends Component & HasComponents> T  createHeader() {
    val result = new Header();
    Slot.Header.set(result);
    return (T) result;
  }

  /**
   * create the navigation component
   * @param <T> the type-parameter of the type of the navigation element
   * @return the navigation element
   */
  @SuppressWarnings("unchecked")
  protected <T extends Component & HasComponents> T  createNavigation() {
    val result = new Nav();
    Slot.Navigation.set(result);
    return (T) result;
  }


}
