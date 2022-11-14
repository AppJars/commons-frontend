package com.appjars.saturn.frontend.layout;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.appjars.saturn.frontend.component.AppNav;
import com.appjars.saturn.frontend.component.AppNavItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;

@SuppressWarnings("serial")
@CssImport("/themes/appjars/base-layout.css")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
public class BaseLayout extends AppLayout {

  @Autowired(required = false)
  final Optional<List<MenuItemProvider>> itemProviders;
  @Autowired(required = false)
  final Optional<DefaultItemProvider> dynamicMenuProvider;
  @Autowired(required = false)
  final Optional<UserAvatarProvider> userAvatarProvider;
  @Autowired(required = false)
  final Optional<ApplicationInfoProvider> applicationInfoProvider;
  @Autowired(required = false)
  final Optional<UserSessionProvider> userSessionProvider;
  
  private H2 viewTitle;
  
  public BaseLayout(
      Optional<UserAvatarProvider> userAvatarProvider,
      Optional<List<MenuItemProvider>> itemProviders, 
      Optional<DefaultItemProvider> dynamicMenuProvider,
      Optional<ApplicationInfoProvider> applicationInfoProvider, 
      Optional<UserSessionProvider> userSessionProvider) {
    this.itemProviders = itemProviders;
    this.dynamicMenuProvider = dynamicMenuProvider;
    this.userAvatarProvider = userAvatarProvider;
    this.applicationInfoProvider = applicationInfoProvider;
    this.userSessionProvider = userSessionProvider;
    
      setPrimarySection(Section.DRAWER);
      addDrawerContent();
      addHeaderContent();
  }

  private void addHeaderContent() {
      DrawerToggle toggle = new DrawerToggle();
      toggle.getElement().setAttribute("aria-label", "Menu toggle");

      viewTitle = new H2();
      viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

      addToNavbar(true, toggle, viewTitle);
  }

  private void addDrawerContent() {
      H2 appName = new H2("Menu");
      appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
      if (applicationInfoProvider.isPresent()) {
        appName.setText(applicationInfoProvider.get().getApplicationTitle());
      }
      Header header = new Header(appName);

      Scroller scroller = new Scroller(createNavigation());

      addToDrawer(header, scroller, createFooter());
  }

  private AppNav createNavigation() {
    // AppNav is not yet an official component.
    // For documentation, visit https://github.com/vaadin/vcf-nav#readme
    AppNav nav = new AppNav();

    if (dynamicMenuProvider.isPresent()) {
      // Default menu handled items
      for (Component menuItem : dynamicMenuProvider.get().getMenuItems()) {
        nav.addItem(menuItem);
      }
    } else {
      if(itemProviders.isPresent()) {
        // Menu Items handled by each module's menu item provider
        for (MenuItemProvider itemProvider : itemProviders.get()) {
          for (Component menuItem : itemProvider.getMenuItems()) {
            // Add all menu items from each menu item provider
            nav.addItem(menuItem);
          }
        }
      } else {
        // No menu item provider present, scanning application's routes
        for (Component menuItem : getMenuItemFromRoutes()) {
          nav.addItem(menuItem);
        }
      }
    }
    return nav;
  }

  private List<AppNavItem> getMenuItemFromRoutes() {
    List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();
    return routes.stream().map(routeData -> {
      String viewName = routeData.getNavigationTarget().getSimpleName();
      String spacedViewName =
          StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(viewName), " ");
      String url = RouteConfiguration.forApplicationScope().getUrl(routeData.getNavigationTarget());

      return new AppNavItem(spacedViewName, url);
    }).collect(Collectors.toList());
  }

  private Footer createFooter() {
      Footer layout = new Footer();
      HorizontalLayout footerLayout = new HorizontalLayout();
      footerLayout.setAlignItems(Alignment.CENTER);
      footerLayout.getElement().getStyle().set("margin", "8px");
      
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
        
        Component userComponent =
            userSessionProvider.isPresent() ? getContextUserMenu(userOpt.get(), avatar)
                : getSimpleUserMenu(userOpt.get(), avatar);

        footerLayout.add(userComponent);
        
      } else {
        // User is not logged in
        if (userSessionProvider.isPresent()) {
          Anchor loginLink = new Anchor(userSessionProvider.get().getLoginUrl(), "Sign in");
          footerLayout.add(loginLink);
        }
      }
      layout.add(footerLayout);
      
      return layout;
  }

  private Component getSimpleUserMenu(Principal principal, Avatar avatar) {
    Div userDiv = new Div();
    userDiv.add(avatar);
    userDiv.add(principal.getName());
    userDiv.getElement().getStyle().set("display", "flex");
    userDiv.getElement().getStyle().set("align-items", "center");
    userDiv.getElement().getStyle().set("gap", "var(--lumo-space-s)");

    return userDiv;
  }

  private Component getContextUserMenu(Principal principal, Avatar avatar) {
    MenuBar userMenu = new MenuBar();
    userMenu.setThemeName("tertiary-inline contrast");

    MenuItem userName = userMenu.addItem("");
    Div div = new Div();
    div.add(avatar);
    div.add(principal.getName());
    div.add(new Icon("lumo", "dropdown"));
    div.getElement().getStyle().set("display", "flex");
    div.getElement().getStyle().set("align-items", "center");
    div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
    userName.add(div);
    userName.getSubMenu().addItem("Sign out", e -> {
      userSessionProvider.get().logout();
    });

    return userMenu;
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
