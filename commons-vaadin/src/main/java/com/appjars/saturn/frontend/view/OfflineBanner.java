package com.appjars.saturn.frontend.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@SuppressWarnings("serial")
@Tag("offline-banner")
@JsModule("./components/offline-banner.js")
@NpmPackage(value = "@polymer/iron-ajax", version = "3.0.1")
public class OfflineBanner extends Component {

}
