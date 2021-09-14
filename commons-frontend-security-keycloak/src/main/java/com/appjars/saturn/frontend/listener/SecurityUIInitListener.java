package com.appjars.saturn.frontend.listener;

import java.nio.file.AccessDeniedException;

import com.appjars.saturn.frontend.spring.SecurityUtils;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;

/**
 * Adds before enter listener to check access to views. Adds the Offline banner.
 *
 */
@SuppressWarnings("serial")
@SpringComponent
public class SecurityUIInitListener implements VaadinServiceInitListener {

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
		  uiEvent.getUI().addBeforeEnterListener(this::beforeEnter);
		});
	}

	/**
	 * Reroutes the user if she is not authorized to access the view.
	 *
	 * @param event before navigation event with event details
	 */
	private void beforeEnter(BeforeEnterEvent event) {
		final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget());
		if (!accessGranted) {
			if (SecurityUtils.isUserLoggedIn()) {
				event.rerouteToError(AccessDeniedException.class);
			} else {
              event.rerouteTo("/");
			}
		}
	}
}
