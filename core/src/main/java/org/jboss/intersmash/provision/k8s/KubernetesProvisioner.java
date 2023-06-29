/**
 * Copyright (C) 2023 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.intersmash.provision.k8s;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.intersmash.application.Application;
import org.jboss.intersmash.application.openshift.HasConfigMaps;
import org.jboss.intersmash.application.openshift.HasSecrets;
import org.jboss.intersmash.k8s.client.Kubernetes;
import org.jboss.intersmash.k8s.client.Kuberneteses;
import org.jboss.intersmash.provision.Provisioner;
import org.jboss.intersmash.provision.openshift.HasPods;
import org.jboss.intersmash.provision.openshift.Scalable;

/**
 * Provisioner that is supposed to deploy an application on Kubernetes.
 */
public interface KubernetesProvisioner<T extends Application> extends Provisioner<T>, Scalable, HasPods {

	Kubernetes kubernetes = Kuberneteses.master();

	@Override
	default void preDeploy() {
		// create secrets
		if (HasSecrets.class.isAssignableFrom(getApplication().getClass())) {
			((HasSecrets) getApplication()).getSecrets().forEach(s -> kubernetes.secrets().create(s));
		}
		// create configMaps
		if (HasConfigMaps.class.isAssignableFrom(getApplication().getClass())) {
			((HasConfigMaps) getApplication()).getConfigMaps().forEach(c -> kubernetes.configMaps().create(c));
		}
	}

	@Override
	default void postUndeploy() {
		// delete secrets
		if (HasSecrets.class.isAssignableFrom(getApplication().getClass())) {
			((HasSecrets) getApplication()).getSecrets().forEach(s -> kubernetes.secrets().delete(s));
		}
		// delete configMaps
		if (HasConfigMaps.class.isAssignableFrom(getApplication().getClass())) {
			((HasConfigMaps) getApplication()).getConfigMaps().forEach(c -> kubernetes.configMaps().delete(c));
		}
	}

	// TODO - check (use a static class method like XTF OpenShift::generateHostName ?)
	default String getUrl(String routeName, boolean secure) {
		String protocol = secure ? "https" : "http";
		return protocol + "://" + kubernetes.getMasterUrl() + "-" + routeName + "-"
				+ kubernetes.getConfiguration().getNamespace();
	}

	@Override
	default URL getURL() {
		try {
			return new URL(getUrl(getApplication().getName(), false));
		} catch (MalformedURLException ex) {
			throw new RuntimeException(
					String.format("Failed to get an URL for the \"%s\" route", this.getClass().getSimpleName()), ex);
		}
	}
}
