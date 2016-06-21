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

	public final static String PROVIDER_ID = "net.dreiucker.javadocextender.elementprovider";

	private static ElementRegistry instance;

	private Map<String, KnownJavaTag> knownTags;

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
		knownTags = new HashMap<>();
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
			Set<String> knownStrings = knownTag.getKnownStrings();
			if (knownStrings != null) {
				for(String string : knownStrings) {
					if (string.startsWith(prefix)) {
						result.add(string);
					}
				}
			}
		}
		
		return result;
	}
	
}
