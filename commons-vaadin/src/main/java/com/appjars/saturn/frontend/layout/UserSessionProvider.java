package com.appjars.saturn.frontend.layout;

public interface UserSessionProvider {

  public void logout();

  public String getBaseUrl();

  public String getLoginUrl();

}
