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
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/KeycloakOperatorApplication.java
package org.jboss.intersmash.application.openshift;
========
package org.jboss.intersmash.tools.application.operator;
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/KeycloakRealmImportOperatorApplication.java

import java.util.Collections;
import java.util.List;

<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/KeycloakOperatorApplication.java
import org.jboss.intersmash.provision.openshift.KeycloakOperatorProvisioner;
========
import org.jboss.intersmash.tools.provision.openshift.KeycloakRealmImportOpenShiftOperatorProvisioner;
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/KeycloakRealmImportOperatorApplication.java
import org.keycloak.k8s.v2alpha1.Keycloak;
import org.keycloak.k8s.v2alpha1.KeycloakRealmImport;

/**
 * End user Application interface which presents Keycloak operator application on OpenShift Container Platform.
 *
 * The application will be deployed by:
 * <ul>
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/KeycloakOperatorApplication.java
 *     <li>{@link KeycloakOperatorProvisioner}</li>
========
 *     <li>{@link KeycloakRealmImportOpenShiftOperatorProvisioner}</li>
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/KeycloakOperatorApplication.java
 * </ul>
 */
public interface KeycloakOperatorApplication extends OperatorApplication {

	Keycloak getKeycloak();

	default List<KeycloakRealmImport> getKeycloakRealmImports() {
		return Collections.emptyList();
	}
}
