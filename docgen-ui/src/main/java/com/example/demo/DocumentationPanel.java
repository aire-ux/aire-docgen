package com.example.demo;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.router.Route;

@Route(value = "main", layout = MainView.class)
public class DocumentationPanel extends AppLayout {

  public DocumentationPanel() {
    addToNavbar(new DrawerToggle());
  }

}
