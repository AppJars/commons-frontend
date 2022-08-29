package com.appjars.saturn.frontend.layout;

import com.vaadin.flow.component.Component;

public interface MenuItemProvider {

  public default Component[] getMenuItems() {
    return new Component[] {};
  }
  
}
