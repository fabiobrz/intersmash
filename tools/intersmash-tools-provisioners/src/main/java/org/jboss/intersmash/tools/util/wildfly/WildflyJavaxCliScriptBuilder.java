package org.jboss.intersmash.tools.util.wildfly;

import java.util.List;

/**
 * Class provide supports for building CLI commands that are used to configure a WildFly Jakarta EE 8/EAP 7.z based
 * application on OpenShift via custom scripts and config map
 */
public class WildflyJavaxCliScriptBuilder extends WildflyAbstractCliScriptBuilder {
	public List<String> build() {
		return build("standalone-openshift.xml");
	}
}
