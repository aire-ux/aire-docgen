package com.example.demo;

import com.vaadin.flow.component.Component;
import javax.annotation.Nonnull;

public class Elements {


  public static final String SLOT;

  static {
    SLOT = "slot";
  }

  @Nonnull
  public static Component setAttribute(@Nonnull Component component, @Nonnull String attributeKey,
      @Nonnull String attributeValue) {
    component.getElement().setAttribute(attributeKey, attributeValue);
    return component;
  }

  /**
   *
   * @param component the component to set the classname on
   * @param className the classname to add
   */
  public static Component addClass(@Nonnull Component component, @Nonnull String className) {
    component.getElement().getClassList().set(className, true);
    return component;
  }


  /**
   * @param component the component to set the classname on
   * @param className the classname to add
   */
  public static Component removeClass(@Nonnull Component component, @Nonnull String className) {
    component.getElement().getClassList().set(className, false);
    return component;
  }


  public enum Attributes {
    Slot(SLOT);


    final String name;

    Attributes(final String attributeName) {
      this.name = attributeName;
    }

    public Component set(Component component, String value) {
      return setAttribute(component, name, value);
    }
  }
}
