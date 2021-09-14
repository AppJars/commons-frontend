package com.appjars.saturn.frontend.util;

import java.util.function.Consumer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VaadinUtils {

  /** Inline configuration helper. Allows writing short one-liners such as {@code add(__(new TextField(), tf -> tf.setWidth("500px")))}
   * Applies the {@code consumer} to the first argument and returns it.
   * 
   * @return {@code t}*/
  public static <T> T __(T t, Consumer<T> consumer) {
    consumer.accept(t);
    return t;
  }

  /**Register a UI event bus that is active while the component is attached.
   * The event bus will unregister itself when the component is detached.
   * @see #fireUIEvent(ComponentEvent)*/
  public static <T extends ComponentEvent<?>> void addUIListener(Component component, Class<T> eventType,
      ComponentEventListener<T> listener) {
    component.addAttachListener(ev -> {
      Registration registration = ComponentUtil.addListener(ev.getUI(), eventType, listener);
      component.addDetachListener(event -> registration.remove());
    });
  }

  /**Trigger an event in the {@linkplain #addUIListener(Component, Class, ComponentEventListener) UI event bus}.**/
  public static void fireUIEvent(ComponentEvent<?> event) {
    UI ui = event.getSource().getUI().get();
    ComponentUtil.fireEvent(ui, event);
  }

}
