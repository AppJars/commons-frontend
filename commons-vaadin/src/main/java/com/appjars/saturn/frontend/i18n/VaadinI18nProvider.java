package com.appjars.saturn.frontend.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Primary
public class VaadinI18nProvider implements I18NProvider {

  protected ResourceBundle resourceBundle;
  ResourceBundle resourceBundleAux;
  Enumeration<URL> enumer;
  InputStream inputStream;
  Locale locale = new Locale("en", "US");
  Properties allProperties = new Properties();
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ByteArrayInputStream bais;

  @Autowired
  public VaadinI18nProvider() throws IOException {
    enumer =
        Thread.currentThread().getContextClassLoader().getResources("messages_en_US.properties");

    while (enumer.hasMoreElements()) {
      URL url = enumer.nextElement();
      inputStream = url.openStream();
      resourceBundleAux = new PropertyResourceBundle(inputStream);
      Enumeration<String> keys = resourceBundleAux.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        allProperties.setProperty(key, resourceBundleAux.getString(key));
      }
    }

    allProperties.store(baos, null);
    bais = new ByteArrayInputStream(baos.toByteArray());
    resourceBundle = new PropertyResourceBundle(bais);
  }

  @Override
  public List<Locale> getProvidedLocales() {
    return List.of(locale);
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    try {
      return MessageFormat.format(resourceBundle.getString(key), params);
    } catch (MissingResourceException e) {
      return key;
    }
  }

}
