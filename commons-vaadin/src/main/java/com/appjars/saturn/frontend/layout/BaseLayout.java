package com.appjars.saturn.frontend.layout;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinRequest;

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
  @Autowired(required = false)
  final Optional<UserAvatarProvider> userAvatarProvider;
  @Autowired(required = false)
  final Optional<ApplicationInfoProvider> applicationInfoProvider;

  private H1 viewTitle;

  public BaseLayout(List<MenuItemProvider> itemProviders,
      Optional<DefaultItemProvider> dynamicMenuProvider,
      Optional<UserAvatarProvider> menuFooterProvider,
      Optional<ApplicationInfoProvider> applicationInfoProvider) {
    this.itemProviders = itemProviders;
    this.dynamicMenuProvider = dynamicMenuProvider;
    this.userAvatarProvider = menuFooterProvider;
    this.applicationInfoProvider = applicationInfoProvider;
    
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
    H2 appName = new H2("AppJars - Menu");
    appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");
    
    if (applicationInfoProvider.isPresent()) {
      appName.setText(applicationInfoProvider.get().getApplicationTitle());
    }

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
    Footer footer = new Footer();
    HorizontalLayout footerLayout = new HorizontalLayout();
    footerLayout.setAlignItems(Alignment.CENTER);
    footerLayout.getElement().getStyle().set("margin", "8px");
    
    //TODO: get login page accordingly
    String loginUrl="login";
    
    Optional<Principal> userOpt =
        Optional.ofNullable(VaadinRequest.getCurrent().getUserPrincipal());

    if (userOpt.isPresent()) {
      // User is logged in
      Avatar avatar = new Avatar();
      
      if (userAvatarProvider.isPresent()) {
        avatar = userAvatarProvider.get().getAvatar();
      }else {
        avatar.setName(userOpt.get().getName());
        avatar.addClassNames("me-xs");
      }
      
      ContextMenu userMenu = new ContextMenu(avatar);
      userMenu.setOpenOnClick(true);
      userMenu.addItem("Logout", e -> {
        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().setLocation(loginUrl);
      });

      Span name = new Span(userOpt.get().getName());
      name.addClassNames("font-medium", "text-s", "text-secondary");

      footerLayout.add(avatar, name);
      
    } else {
      // User is not logged in
      Anchor loginLink = new Anchor(loginUrl, "Sign in");
      footerLayout.add(loginLink);
    }
    
    footer.add(footerLayout);
    return footer;
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
