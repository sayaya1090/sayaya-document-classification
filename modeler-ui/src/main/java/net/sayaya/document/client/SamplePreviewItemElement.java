package net.sayaya.document.client;

import elemental2.dom.File;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.HtmlContentBuilder;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class SamplePreviewItemElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewItemElement> {
	public static SamplePreviewItemElement instance(File file) {
		SamplePreviewItemElement elem = new SamplePreviewItemElement(div(), file.name);
		elem.state = PreviewItemState.UPLOADING;
		return elem;
	}
	public static SamplePreviewItemElement instance(Sample sample) {
		return new SamplePreviewItemElement(div(), sample.name());
	}
	private final HtmlContentBuilder<HTMLLabelElement> name = label();
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private PreviewItemState state;
	private SamplePreviewItemElement(HtmlContentBuilder<HTMLDivElement> e, String fileName) {
		super(e.css("preview-item"));
		_this = e;
		layout();
		name.add(fileName);
		state = PreviewItemState.LOADING;
	}

	private void layout() {
		_this.add(name);
	}

	@Override
	public SamplePreviewItemElement that() {
		return this;
	}

	private enum PreviewItemState {
		LOADING, LOADED, UPLOADING
	}
}
