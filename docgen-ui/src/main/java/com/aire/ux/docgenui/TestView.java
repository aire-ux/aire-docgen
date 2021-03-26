package com.aire.ux.docgenui;

import com.aire.ux.component.Button;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.router.Route;

@Route()
public class TestView extends Section {

  public TestView(

  ) {
    add(new Button());
  }
}
