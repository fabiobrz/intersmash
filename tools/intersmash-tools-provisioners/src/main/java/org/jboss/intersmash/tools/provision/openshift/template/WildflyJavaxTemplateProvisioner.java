package org.jboss.intersmash.tools.provision.openshift.template;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.intersmash.tools.IntersmashConfig;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.TagImportPolicyBuilder;

public class WildflyJavaxTemplateProvisioner implements OpenShiftTemplateProvisioner {

	// supported product versions
	// EAP
	public static final String SUPPORTED_EAP_VERSION_73 = "eap73";
	public static final String SUPPORTED_EAP_VERSION_74 = "eap74";
	public static final String SUPPORTED_EAP_VERSION_XP3 = "eap-xp3";
	public static final String SUPPORTED_EAP_VERSION_XP4 = "eap-xp4";
	public static final String SUPPORTED_JDK_OPENJDK_8 = "openjdk8";
	public static final String SUPPORTED_JDK_OPENJDK_11 = "openjdk11";
	public static final String SUPPORTED_JDK_OPENJDK_17 = "openjdk17";

	public static final String[] SUPPORTED_EAP_VERSIONS = { SUPPORTED_EAP_VERSION_73, SUPPORTED_EAP_VERSION_74,
			SUPPORTED_EAP_VERSION_XP3, SUPPORTED_EAP_VERSION_XP4 };
	public static final String[] SUPPORTED_JDK_VERSIONS = { SUPPORTED_JDK_OPENJDK_8, SUPPORTED_JDK_OPENJDK_11,
			SUPPORTED_JDK_OPENJDK_17 };

	@Override
	public String getTemplatesUrl() {
		return IntersmashConfig.wildflyJakartaEe8Templates();
	}

	@Override
	public String getTemplateFileName(OpenShiftTemplate openShiftTemplate) {
		final String eapProductCode = getProductCode();
		final String eapJdk = getEapJdk();
		// validate used EAP image product code is allowed
		if (!List.of(SUPPORTED_EAP_VERSIONS).stream().anyMatch(allowed -> allowed.equals(eapProductCode))) {
			throw new IllegalStateException(String.format("Unsupported EAP product code: %s", eapProductCode));
		}

		// validate used eap image JDK is allowed
		if (!List.of(SUPPORTED_JDK_VERSIONS).stream().anyMatch(allowed -> allowed.equals(eapJdk))) {
			throw new IllegalStateException("Unsupported JDK version: " + eapJdk);
		}

		if (eapProductCode.equals(SUPPORTED_EAP_VERSION_73)) {
			if (eapJdk.equals(SUPPORTED_JDK_OPENJDK_11)) {
				// add the JDK part to the image filename suffix since this will be needed to find EAP 73
				// OpenJDK 11 based template files, e.g.:
				// https://raw.githubusercontent.com/jboss-container-images/jboss-eap-7-openshift-image/eap73/templates/eap73-openjdk11-basic-s2i.json
				// Given that we already validated the allowed values, it is safe to assume that we're dealing with
				// OpenJDK 11 here
				return String.format("%s-%s-%s-s2i", eapProductCode, SUPPORTED_JDK_OPENJDK_11,
						openShiftTemplate.getLabel());
			}
		}
		// Don't add any JDK info when dealing with EAP 73 OpenJDK 8 based images or EAP 74 OpenJDK based images since
		// template files never include the OpenJDK info, e.g.:
		// https://raw.githubusercontent.com/jboss-container-images/jboss-eap-openshift-templates/eap74/templates/eap74-basic-s2i.json
		// https://raw.githubusercontent.com/jboss-container-images/jboss-eap-openshift-templates/eap74/templates/eap74-https-s2i.json
		// https://raw.githubusercontent.com/jboss-container-images/jboss-eap-7-openshift-image/eap73/templates/eap73-basic-s2i.json
		return String.format("%s-%s-s2i", eapProductCode, openShiftTemplate.getLabel());
	}

	/**
	 * This is a bit different for EAP since the image stream template contains two image streams, one for builder
	 * image, the other for runtime image. Create both image streams here, but return the list of builder image references
	 * in order to be aligned with implementations for other products.
	 * <p>
	 * Update only DockerImage based tags, as the others are just tag references.
	 *
	 * @return list of builder image stream references
	 */
	@Override
	public List<ImageStream> deployImageStreams() {
		List<ImageStream> streams = new ArrayList<>(2);
		String url = getUsedImageStreamUrl();
		try (InputStream is = new URL(url).openStream()) {
			KubernetesResourceList<ImageStream> kubernetesList = openShift.imageStreams().list();
			for (HasMetadata item : kubernetesList.getItems()) {
				if (item.getMetadata().getName().contains("runtime")) {
					ImageStream runtimeImageStream = (ImageStream) item;
					// update the DockerImage based tags with EAP runtime image set by configuration
					runtimeImageStream.getSpec().getTags().stream()
							.filter(tagReference -> tagReference.getFrom().getKind().equals("DockerImage"))
							.forEach(tagReference -> {
								tagReference.getFrom().setName(IntersmashConfig.wildflyJakartaEe8ImageURL());
								tagReference.setImportPolicy(new TagImportPolicyBuilder().withInsecure(true).build());
							});
					streams.add(openShift.imageStreams().createOrReplace(runtimeImageStream));
				} else {
					ImageStream imageStream = (ImageStream) item;
					// update the DockerImage based tags with EAP builder image set by configuration
					imageStream.getSpec().getTags().stream()
							.filter(tagReference -> tagReference.getFrom().getKind().equals("DockerImage"))
							.forEach(tagReference -> {
								tagReference.getFrom().setName(IntersmashConfig.wildflyJakartaEe8ImageURL());
								tagReference.setImportPolicy(new TagImportPolicyBuilder().withInsecure(true).build());
							});
					streams.add(openShift.imageStreams().createOrReplace((ImageStream) item));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to deploy EAP 7 image streams from " + url, e);
		}
		return streams;
	}

	@Override
	public String getProductCode() {
		return IntersmashConfig.getProductCode(IntersmashConfig.wildflyJakartaEe8ImageURL());
	}

	/**
	 * Here the actual URL for the used EAP 7 image streams files, depending on the supported EAP 7 version
	 *
	 * @return String that represents the URL to the actual EAP 7 image streams files, depending on the supported EAP 7
	 * version
	 */
	private String getUsedImageStreamUrl() {

		final String imageStreamsFileNameSuffix = "-image-stream.json";
		final String eapProductCode = getProductCode();
		final String eapJdk = getEapJdk();

		// validate used EAP image product code is allowed
		if (!List.of(SUPPORTED_EAP_VERSIONS).stream().anyMatch(allowed -> allowed.equals(eapProductCode))) {
			throw new IllegalStateException(String.format("Unsupported EAP product code: %s", eapProductCode));
		}

		// validate used eap image JDK is allowed
		if (!List.of(SUPPORTED_JDK_VERSIONS).stream().anyMatch(allowed -> allowed.equals(eapJdk))) {
			throw new IllegalStateException("Unsupported JDK version: " + eapJdk);
		}

		switch (eapProductCode) {
			case SUPPORTED_EAP_VERSION_73:
				if (eapJdk.equals(SUPPORTED_JDK_OPENJDK_8)) {
					// don't add any JDK info, IS file names for EAP 73 OpenJDK 8 based images store no JDK information
					// (e.g.: https://github.com/jboss-container-images/jboss-eap-7-openshift-image/blob/7.3.x/templates/eap73-image-stream.json)
					return getImageStreamsUrl() + eapProductCode + imageStreamsFileNameSuffix;
				} else {
					// add the JDK part to the image filename suffix since this will be needed to find EAP 73
					// OpenJDK 11 based image streams files, e.g.:
					// https://github.com/jboss-container-images/jboss-eap-7-openshift-image/blob/7.3.x/templates/eap73-openjdk11-image-stream.json
					// Given that we already validated the allowed values, it is safe to assume that we're dealing with
					// OpenJDK 11 here
					return getImageStreamsUrl() + eapProductCode
							+ String.format("-%s", SUPPORTED_JDK_OPENJDK_11)
							+ imageStreamsFileNameSuffix;
				}
			case SUPPORTED_EAP_VERSION_XP3:
				// When dealing with EAP XP3 OpenJDK based images then the IS file includes the OpenJDK info, e.g.:
				// https://github.com/jboss-container-images/jboss-eap-openshift-templates/blob/eap-xp3/jboss-eap-xp3-openjdk11-openshift.json
				return getImageStreamsUrl() + "jboss-" + eapProductCode
						+ String.format("-%s", eapJdk) + "-openshift.json";
			default:
				// When dealing with EAP 74/XP4 OpenJDK based images then IS files always include the OpenJDK info, e.g.:
				// https://github.com/jboss-container-images/jboss-eap-openshift-templates/blob/eap74/eap74-openjdk11-image-stream.json
				// https://github.com/jboss-container-images/jboss-eap-openshift-templates/blob/eap74/eap74-openjdk8-image-stream.json
				// https://github.com/jboss-container-images/jboss-eap-openshift-templates/blob/eap-xp4/eap-xp4-openjdk11-image-stream.json
				// TODO - remove once there will be a JDK 17 IS
				final String actualEapJdk = SUPPORTED_JDK_OPENJDK_17.equals(eapJdk)
						? SUPPORTED_JDK_OPENJDK_11
						: eapJdk;
				return getImageStreamsUrl() + eapProductCode
						+ String.format("-%s", actualEapJdk) + imageStreamsFileNameSuffix;
		}
	}

	private String getImageStreamsUrl() {
		return IntersmashConfig.wildflyJakartaEe8ImageStreams();
	}

	private static String getEapJdk() {
		final String image = IntersmashConfig.wildflyImageURL();
		final String eapProductCode = IntersmashConfig.getProductCode(IntersmashConfig.wildflyImageURL());

		if (image.matches(".*" + eapProductCode + "-openjdk8.*")) {
			return SUPPORTED_JDK_OPENJDK_8;
		} else if (image.matches(".*" + eapProductCode + "-openjdk\\d\\d.*")) {
			return image.replaceFirst(".*" + eapProductCode + "-openjdk(\\d\\d).*", "openjdk$1");
		} else {
			throw new IllegalStateException(String.format("Unsupported JDK: %s", image));
		}
	}
}
