package com.example.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;
import lombok.val;

/**
 * @group Layouts
 * @component MainView <code lang="groovy">
 * </code>
 * @property name hello
 */
@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/application.css")
public class MainView extends StandardApplicationLayout implements AppShellConfigurator,
    RouterLayout {

  public MainView() {
    getElement().setProperty("width", "950px");

    val button = new Button(new Icon(VaadinIcon.MENU));
    addToHeader(button);

    button.addClickListener(e -> {
      openNavigation();
    });

    val close = new Button(new Icon(VaadinIcon.CLOSE));
    close.setClassName("control");
    close.addClickListener(event -> {
      closeNavigation();

    });

    val header = new Header();
    header.add(close);
    addToNavigation(header);

    addNavigationStateChangeListener(evt -> {
      System.out.println("EVENT " + evt);
    });


  }
}
