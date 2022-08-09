package com.appjars.saturn.frontend.view;

import com.vaadin.flow.component.Component;

public interface MenuItemProvider {

  public default Component[] getMenuItems() {
    return new Component[] {};
  }
  
}
