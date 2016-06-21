package net.dreiucker.javadocextender.provider;

import java.util.HashSet;
import java.util.Set;

import net.dreiucker.javadocextender.extensionpoint.IElementChangeListener;
import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

/**
 * Provides dummy elements for the javadoc.
 * This class is for testing and experimenting purposes.
 * 
 * @author Mark
 *
 */
public class DummyElementProvider3 implements IElementProvider {
	
	private final String VALID_TAG = "prefixAnotherTag";

	public DummyElementProvider3() {
	}

	@Override
	public String getTag() {
		return VALID_TAG;
	}

	@Override
	public Set<String> getKnownElements() {
		HashSet<String> set = new HashSet<>();
		set.add("ValidElement1");
		set.add("ValidElement2");
		set.add("ValidElement3");
		return set;
	}

	@Override
	public boolean unknownElementsAllowed() {
		return false;
	}

	@Override
	public void addElementsChangedListener(IElementChangeListener listener) {
		// no, changes are not supported, so no need for listeners
	}

	@Override
	public void removeElementsChangedListener(IElementChangeListener listener) {
		// no, changes are not supported, so no need for listeners
	}

}
