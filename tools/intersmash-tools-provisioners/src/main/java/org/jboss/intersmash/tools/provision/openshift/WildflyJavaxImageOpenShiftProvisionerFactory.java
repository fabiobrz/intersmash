package org.jboss.intersmash.tools.provision.openshift;

import org.jboss.intersmash.tools.application.Application;
import org.jboss.intersmash.tools.application.openshift.WildflyJavaxImageOpenShiftApplication;
import org.jboss.intersmash.tools.provision.ProvisionerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WildflyJavaxImageOpenShiftProvisionerFactory implements ProvisionerFactory<WildflyJavaxImageOpenShiftProvisioner> {

	@Override
	public WildflyJavaxImageOpenShiftProvisioner getProvisioner(Application application) {
		if (WildflyJavaxImageOpenShiftApplication.class.isAssignableFrom(application.getClass()))
			return new WildflyJavaxImageOpenShiftProvisioner((WildflyJavaxImageOpenShiftApplication) application);
		return null;
	}
}
