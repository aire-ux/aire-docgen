package com.example.demo;

import com.example.demo.Elements.Attributes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.shared.Registration;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.val;

@Tag("standard-application-layout")
@JsModule("./src/layouts/standard-application-layout.ts")
@NpmPackage(value = "lit-element", version = "2.5.1")
public class StandardApplicationLayout extends HtmlContainer {

  /**
   * slot-name for header
   */
  static final String HEADER = "header";
  /**
   * slot name for navigation
   */
  static final String NAVIGATION = "navigation";
  /**
   * event names
   */

  private static final String NAVIGATION_STATE_CHANGED_EVENT = "navigation-state-changed";
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


  /**
   * @return the navigation component
   */
  @SuppressWarnings("unchecked")
  public <T extends Component & HasComponents> T getNavigation() {
    return (T) navigation;
  }

  /**
   * @param slot       the slot to add the component to
   * @param components the components to place into the slot
   */
  public void add(Slot slot, Component... components) {
    switch (slot) {
      case Header:
        addToHeader(components);
        return;
      case Navigation:
        addToNavigation(components);
        return;
    }
  }


  public Registration addNavigationStateChangeListener(
      ComponentEventListener<NavigationStateChangeEvent> listener) {
    return addListener(NavigationStateChangeEvent.class, listener);
  }

  /**
   * add components to the navigation element
   *
   * @param components the components to add to the navigation element
   */
  public void addToNavigation(@Nonnull Component... components) {
    navigation.add(components);
  }

  /**
   * same as calling add(Slot.Header, components...)
   *
   * @param components th
   */
  public void addToHeader(@Nonnull Component... components) {
    header.add(components);
  }

  /**
   * create a header component
   *
   * @param <T> the type-parameter of the type of the header element
   * @return the header element (should call Slot.Header.set(result)
   */

  @SuppressWarnings("unchecked")
  protected <T extends Component & HasComponents> T createHeader() {
    val result = new Header();
    Slot.Header.set(result);
    return (T) result;
  }

  /**
   * create the navigation component
   *
   * @param <T> the type-parameter of the type of the navigation element
   * @return the navigation element
   */
  @SuppressWarnings("unchecked")
  protected <T extends Component & HasComponents> T createNavigation() {
    val result = new Nav();
    Slot.Navigation.set(result);
    return (T) result;
  }

  /**
   * open navigation
   */
  public void openNavigation() {
    getElement().executeJs("this.openNavigation()");
  }

  /**
   * close navigation
   */
  public void closeNavigation() {
    getElement().executeJs("this.closeNavigation()");
  }


  public enum Mode {


    Overlay,


    Push
  }

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

    Slot(String name, String... additionalClasses) {
      this.name = name;
      this.additionalClasses = additionalClasses;
    }

    public Component set(Component component) {
      Attributes.Slot.set(component, name);
      for (val className : additionalClasses) {
        Elements.addClass(component, className);
      }
      return component;
    }
  }

  /**
   * event classes
   */


  public enum NavigationState {
    Open,
    Closed;

    public static NavigationState from(@Nonnull String value) {
      for (val state : values()) {
        if (state.name().equalsIgnoreCase(value)) {
          return state;
        }
      }
      throw new NoSuchElementException("No state value '" + value + "'");
    }
  }

  /**
   *
   */
  @DomEvent(value = "navigation-state-changed")
  public static class NavigationStateChangeEvent extends ComponentEvent<Component> {

    @Getter
    private NavigationState state;

    public NavigationStateChangeEvent(
        Component source,
        boolean fromClient,
        @EventData("element.navigationState") String state
    ) {
      super(source, fromClient);
      this.state = NavigationState.from(state);
    }
  }

}
