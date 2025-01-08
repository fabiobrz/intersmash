/**
 * Copyright (C) 2024 Red Hat, Inc.
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
package org.jboss.intersmash.provision.operator.model.odh;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.opendatahub.features.v1.FeatureTracker;

/**
 * Used by {@link org.jboss.intersmash.provision.operator.OpenDataHubOperatorProvisioner} client methods,
 * this class represents a concrete {@link java.util.List} of {@link FeatureTracker} instances.
 */
public class FeatureTrackerList extends CustomResourceList<FeatureTracker> {
}
