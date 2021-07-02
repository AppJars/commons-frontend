package com.appjars.saturn.frontend.view;

import com.appjars.saturn.frontend.spring.SecurityUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "logout")
@PageTitle("Logout")
public class LogoutView extends Div {

  public LogoutView() {
    if (!SecurityUtils.isUserLoggedIn()) {
      UI.getCurrent().getPage().setLocation("/");
    } else {
      Dialog d = new Dialog();
      d.setCloseOnEsc(false);
      d.setCloseOnOutsideClick(false);
      d.setModal(true);
      VerticalLayout vl = new VerticalLayout();
      Span message = new Span("Are you sure that you want to logout?");
      HorizontalLayout hl = new HorizontalLayout();
      Button confirm = new Button("Yes");
      confirm.addClickListener(ev -> {
        UI.getCurrent().getPage().setLocation("/sso/logout");
      });
      Button deny = new Button("No");
      deny.addClickListener(ev -> {
        d.close();
        UI.getCurrent().navigate("");
      });
      hl.add(confirm, deny);
      hl.setWidthFull();
      hl.setJustifyContentMode(JustifyContentMode.CENTER);
      vl.add(message, hl);
      d.add(vl);
      d.open();
    }
  }
}
