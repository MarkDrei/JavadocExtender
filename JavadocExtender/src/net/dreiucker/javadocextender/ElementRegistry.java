package net.dreiucker.javadocextender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;

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

	public final static String PROVIDER_ID = "net.dreiucker.javadocextender.elementprovider";

	private static final boolean DEBUG = true;

	private static ElementRegistry instance;

	private Map<String, KnownJavaTag> knownTags;

	/**
	 * @return the singleton instance of the registry
	 */
	public static ElementRegistry getInstance() {
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
		knownTags = new HashMap<>();
		IConfigurationElement[] configurationElements =  RegistryFactory.getRegistry()
				.getConfigurationElementsFor(PROVIDER_ID);
		for (IConfigurationElement element : configurationElements) {
			if (DEBUG) {
				System.out.println("JavadocExtender registry, new configuration element: " + element);
			}
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
		KnownJavaTag knownJavaTag = new KnownJavaTag(provider);
		
		String tag = provider.getTag();
		KnownJavaTag putResult = knownTags.put(tag, knownJavaTag);
		if (putResult != null && !(provider.equals(putResult.getProvider()))) {
			System.out.println("JavadocProvider WARNING: Javadoc tag \"" + tag + "\" is already known. Contents will be overwritten");
		}
	}
	
	/**
	 * Looks up all tags which start with the given prefix
	 * @param prefix without @ sign
	 * @return all tags which start with the given prefix
	 */
	public List<String> getAllTagsWithPrefix(String prefix) {
		ArrayList<String> result = new ArrayList<String>();
		for (String tag : knownTags.keySet()) {
			if (tag.startsWith(prefix)) {
				result.add(tag);
			}
		}
		return result;
	}

	public List<String> getAllKnownValues(String tagname, String prefix) {
		KnownJavaTag knownTag = knownTags.get(tagname);
		ArrayList<String> result = new ArrayList<>();
		
		if (knownTag != null) {
			result.addAll(knownTag.getCompletionProposals(prefix));
		}
		
		return result;
	}
	
	public boolean isKnownTag(String tagName) {
		return knownTags.containsKey(tagName);
	}
	
	public boolean isElementValid(String tagName, String text) {
		KnownJavaTag knownJavaTag = knownTags.get(tagName);
		if (knownJavaTag != null) {
			if (knownJavaTag.isUnknownStringAllowed()) {
				return true;
			}
			return knownJavaTag.isKnownValue(text);
		}
		return false;
	}
	
	/**
	 * Gets the element provider, if there is one which handles exactly this tag
	 * and element combination
	 * 
	 * @param tagname
	 *            The name of the tag, without the preceding "@"
	 * @param value
	 *            The value behind the tag
	 * @return The IElementProvider which can provide additional information, or
	 *         <code>null</code> if none exists
	 */
	public IElementProvider getProviderForTag(String tagname, String value) {
		KnownJavaTag knownTag = knownTags.get(tagname);
		if (knownTag != null) {
			if (knownTag.isKnownValue(value)) {
				return knownTag.getProvider();
			}
		}
		return null;
	}

	/**
	 * Get the description for a value
	 * 
	 * @param tagname The name of the tag to which the value belongs 
	 * @param value the value
	 * @return a description for the value or null, if none is available
	 */
	public String getDescription(String tagname, String value) {
		KnownJavaTag knownTag = knownTags.get(tagname);
		if (knownTag != null) {
			return knownTag.getDescription(value);
		}
		return null;
	}
}
