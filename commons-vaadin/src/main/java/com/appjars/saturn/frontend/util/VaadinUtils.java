package com.appjars.saturn.frontend.util;

import java.util.function.Consumer;
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

  }
