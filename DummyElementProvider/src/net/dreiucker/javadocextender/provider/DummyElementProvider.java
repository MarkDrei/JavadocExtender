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
public class DummyElementProvider implements IElementProvider {
	
	private final String VALID_TAG = "dummy.Tag";

	public DummyElementProvider() {
	}

	@Override
	public String getTag() {
		return VALID_TAG;
	}

	@Override
	public Set<String> getKnownElements() {
		HashSet<String> set = new HashSet<>();
		return set;
	}

	@Override
	public boolean unknownElementsAllowed() {
		return true;
	}

	@Override
	public void addElementsChangedListener(IElementChangeListener listener) {
		// no, changes are not supported, so no need for listeners
	}

	@Override
	public void removeElementsChangedListener(IElementChangeListener listener) {
		// no, changes are not supported, so no need for listeners
	}

	@Override
	public void openEditor(String text) {
		// no dummy implementation
	}

	@Override
	public String getElementDescription(String element) {
		return null;
	}

}
