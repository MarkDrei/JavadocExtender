package net.dreiucker.javadocextender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

/**
 * <p>
 * This class knows and handles all known javadoc elements which are available
 * through the javadoc extender.
 * </p>
 * 
 * <p>
 * The class is singleton and, upon construction, collects valid elements from
 * all the providers.
 * </p>
 * 
 * @author Mark
 *
 */
public class ElementRegistry {

	private class KnownJavaTag {
		// known strings behind the @tag
		Set<String> knownStrings;
		boolean unknownStringsAllowed;
	}

	public final static String PROVIDER_ID = "net.dreiucker.javadocextender.elementprovider";

	private static ElementRegistry instance;

	private Map<String, KnownJavaTag> tags;

	/**
	 * @return the singleton instance of the registry
	 */
	static ElementRegistry getInstance() {
		if (instance == null) {
			synchronized(ElementRegistry.class) {
				if (instance == null) {
					instance = new ElementRegistry();
				}
			}
		}
		return instance;
	}

	/**
	 * Not to be called by clients
	 */
	private ElementRegistry() {
		tags = new HashMap<>();
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PROVIDER_ID);
		for (IConfigurationElement element : configurationElements) {
			try {
				Object extension = element.createExecutableExtension("providerclass");
				if (extension instanceof IElementProvider) {
					addContributions((IElementProvider) extension);
				}

			} catch (CoreException e) {
				System.err.println("Failed to create executable extension: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}

	}

	private void addContributions(IElementProvider provider) {
		Set<String> validTags = provider.getValidTags();
		for (String validTag : validTags) {
			Set<String> knownElements = provider.getKnownElements(validTag);
			if (tags.containsKey(validTag)) {

				System.out.println("WARNING: Tag " + validTag + " is already known. Contents will be merged");
				
				KnownJavaTag knownTag = tags.get(validTag);
				knownTag.knownStrings.addAll(knownElements);
				knownTag.unknownStringsAllowed |= provider.unknownElementsAllowed(validTag);
			} else {
				KnownJavaTag knownJavaTag = new KnownJavaTag();
				knownJavaTag.knownStrings = knownElements;
				knownJavaTag.unknownStringsAllowed = provider.unknownElementsAllowed(validTag);
				tags.put(validTag, knownJavaTag);
			}
		}
	}
	
	/**
	 * Looks up all tags which start with the given prefix
	 * @param prefix without @ sign
	 * @return all tags which start with the given prefix
	 */
	public List<String> getAllTagsWithPrefix(String prefix) {
		ArrayList<String> result = new ArrayList<String>();
		for (String tag : tags.keySet()) {
			if (tag.startsWith(prefix)) {
				result.add(tag);
			}
		}
		return result;
	}

	public List<String> getAllKnownValues(String tagname, String prefix) {
		KnownJavaTag knownTags = tags.get(tagname);
		ArrayList<String> result = new ArrayList<>();
		
		if (knownTags != null) {
			for(String string : knownTags.knownStrings) {
				if (string.startsWith(prefix)) {
					result.add(string);
				}
			}
		}
		
		return result;
	}
	

}
