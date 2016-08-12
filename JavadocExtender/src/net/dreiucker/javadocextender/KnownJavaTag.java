package net.dreiucker.javadocextender;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.dreiucker.javadocextender.extensionpoint.IElementChangeListener;
import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

/**
 * A tag (@someName) inside the javadoc along with functionality to manage valid
 * contents behind that tag
 * 
 * @author Mark
 *
 */
class KnownJavaTag implements IElementChangeListener {
	
	// The provider of the tag
	private IElementProvider provider;
	
	private boolean isKnownStringsUpToDate;
	
	// known strings behind the @tag
	private Set<String> knownStrings = null;
	
	/**
	 * @param provider
	 *            The provider for this element, <code>null</code> not allowed
	 */
	public KnownJavaTag(IElementProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("null now allowed");
		}
		this.provider = provider;
		isKnownStringsUpToDate = false;
	}
	
	/**
	 * Check whether unknown names behind the tag name should be allowed or treated as an error
	 * 
	 * @return
	 */
	public boolean isUnknownStringAllowed() {
		return provider.unknownElementsAllowed();
	}
	
	public IElementProvider getProvider() {
		return provider;
	}
	
	@Override
	public void knownElementsChanged() {
		isKnownStringsUpToDate = false;
	}

	public boolean isKnownValue(String value) {
		updateKnownStrings();
		return knownStrings.contains(value);
	}

	private void updateKnownStrings() {
		if (knownStrings == null || !isKnownStringsUpToDate) {
			knownStrings = provider.getKnownElements();
			isKnownStringsUpToDate = true;
		}
		if (knownStrings == null) {
			knownStrings = new HashSet<>();
		}
	}

	/**
	 * Completes all proposals which start with the given prefix
	 * @param prefix
	 * @return
	 */
	public Collection<? extends String> getCompletionProposals(String prefix) {
		updateKnownStrings();
		HashSet<String> result = new HashSet<>();
		for(String string : knownStrings) {
			if (string.startsWith(prefix)) {
				result.add(string);
			}
		}
		return result;
	}
	
	/**
	 * Get the description for an element
	 * 
	 * @param value
	 *            The value whose description is requested
	 * @return The description for an element or <code>null</code> if no
	 *         description is available
	 */
	public String getDescription(String value) {
		return provider.getElementDescription(value);
	}

}