package com.aire.ux.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;

@Tag("div")
public class Button extends Div {

  public Button() {
    add(new NativeButton("hello"));
  }
}
