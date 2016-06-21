package net.dreiucker.javadocextender;

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
	
	public Set<String> getKnownStrings() {
		if (knownStrings == null || !isKnownStringsUpToDate) {
			knownStrings = provider.getKnownElements();
			isKnownStringsUpToDate = true;
		}
		return knownStrings;
	}
	
	@Override
	public void knownElementsChanged() {
		isKnownStringsUpToDate = false;
	}

}