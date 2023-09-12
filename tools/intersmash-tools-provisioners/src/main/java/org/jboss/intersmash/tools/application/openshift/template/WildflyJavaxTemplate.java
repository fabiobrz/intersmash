package org.jboss.intersmash.tools.application.openshift.template;

import org.jboss.intersmash.tools.provision.openshift.template.OpenShiftTemplate;

/**
 * OpenShift template for WildFly Jakarta EE 8 (<= 26.1.2)/EAP 7.z applications.
 * <p>
 * See e.g.: https://github.com/jboss-container-images/jboss-eap-7-openshift-image
 */
public enum WildflyJavaxTemplate implements OpenShiftTemplate {
	AMQ_PERSISTENT("amq-persistent"),
	AMQ("amq"),
	BASIC("basic"),
	HTTPS("https"),
	SSO("sso");

	private String name;

	WildflyJavaxTemplate(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return name;
	}
}
