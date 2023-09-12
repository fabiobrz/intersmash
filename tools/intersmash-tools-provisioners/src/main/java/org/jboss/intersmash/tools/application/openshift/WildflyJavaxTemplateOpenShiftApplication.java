package org.jboss.intersmash.tools.application.openshift;

import org.jboss.intersmash.tools.application.openshift.template.WildflyJavaxTemplate;

/**
 * End user Application descriptor interface which presents EAP 7 template application on OpenShift Container Platform.
 *
 * See {@link org.jboss.intersmash.tools.application.openshift.template.WildflyJavaxTemplate} for available templates the
 * application can represent.
 *
 * The application will be deployed by:
 * <ul>
 *     <li>{@link org.jboss.intersmash.tools.provision.openshift.WildflyJavaxTemplateOpenShiftProvisioner}</li>
 * </ul>
 */
public interface WildflyJavaxTemplateOpenShiftApplication
		extends WildflyOpenShiftApplication, TemplateApplication<WildflyJavaxTemplate> {

}
