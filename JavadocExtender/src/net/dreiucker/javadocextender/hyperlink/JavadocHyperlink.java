package net.dreiucker.javadocextender.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

public class JavadocHyperlink implements IHyperlink {

	private String text;
	private IRegion region;
	private IElementProvider provider;

	public JavadocHyperlink(IRegion region, String text, IElementProvider provider) {
		if (region == null || text == null || provider == null) {
			throw new IllegalArgumentException("null parameter not allowed");
		}
		this.region = region;
		this.text = text;
		this.provider = provider;
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "This is the label";
	}

	@Override
	public String getHyperlinkText() {
		return text;
	}

	@Override
	public void open() {
		provider.openEditor(text);
	}

}
