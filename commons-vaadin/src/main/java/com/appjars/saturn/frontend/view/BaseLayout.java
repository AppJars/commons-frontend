package com.appjars.saturn.frontend.view;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "./themes/appjars/base-layout.css")
@SuppressWarnings("serial")
public class BaseLayout extends AppLayout {
  
  @Autowired
  final List<MenuItemProvider> itemProviders;
  @Autowired(required = false)
  final Optional<DefaultItemProvider> dynamicMenuProvider;
  
  final Optional<MenuFooterProvider> menuFooterProvider;
  
  private H1 viewTitle;

  public BaseLayout(List<MenuItemProvider> itemProviders,
      Optional<DefaultItemProvider> dynamicMenuProvider, Optional<MenuFooterProvider> menuFooterProvider) {
    this.itemProviders = itemProviders;
    this.dynamicMenuProvider = dynamicMenuProvider;
    this.menuFooterProvider = menuFooterProvider;
    
    setPrimarySection(Section.DRAWER);
    addToNavbar(true, createHeaderContent());
    addToDrawer(createDrawerContent());
  }

  private Component createHeaderContent() {
    DrawerToggle toggle = new DrawerToggle();
    toggle.addClassName("text-secondary");
    toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    toggle.getElement().setAttribute("aria-label", "Menu toggle");

    viewTitle = new H1();
    viewTitle.addClassNames("m-0", "text-l");

    Header header = new Header(toggle, viewTitle);
    header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl",
        "items-center", "w-full");
    return header;
  }

  private Component createDrawerContent() {
    H2 appName = new H2("AppJars - Menu"); //TODO Add custom title
    appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

    com.vaadin.flow.component.html.Section section =
        new com.vaadin.flow.component.html.Section(appName, createNavigation(), createFooter());
    section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
    return section;
  }

  private Nav createNavigation() {
    Nav nav = new Nav();
    nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
    nav.getElement().setAttribute("aria-labelledby", "views");

    // Wrap the links in a list; improves accessibility
    UnorderedList list = new UnorderedList();
    list.addClassNames("list-none", "m-0", "p-0");
    nav.add(list);

    if (dynamicMenuProvider.isPresent()) {
      // Default menu handled items
      for (Component menuItem : dynamicMenuProvider.get().getMenuItems()) {
        list.add(menuItem);
      }
    } else {
      // Menu Items handled by each module's menu item provider
      for (MenuItemProvider itemProvider : itemProviders) {
        for (Component menuItem : itemProvider.getMenuItems()) {
          // Add all menu items from each menu item provider
          list.add(menuItem);
        }
      }
    }
    return nav;
  }

  private Footer createFooter() {
    Footer layout = new Footer();

    if (menuFooterProvider.isPresent()) {
      layout = menuFooterProvider.get().getFooter();
    }

    layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");
    return layout;
  } 

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());
  }

  private String getCurrentPageTitle() {
    PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
    return title == null ? "" : title.value();
  }
}
