package com.appjars.saturn.frontend.listener;

import java.nio.file.AccessDeniedException;

import com.appjars.saturn.frontend.view.OfflineBanner;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * Adds before enter listener to check access to views. Adds the Offline banner.
 *
 */
@SuppressWarnings("serial")
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
			uiEvent.getUI().add(new OfflineBanner());
		});
	}

}
