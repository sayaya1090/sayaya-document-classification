package net.sayaya.document.client;

import elemental2.dom.HTMLDivElement;
import net.sayaya.document.data.Model;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.HtmlContentBuilder;

public class SamplePreviewItemElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewItemElement> implements HasSelectionChangeHandlers<Model> {
	private final HtmlContentBuilder<HTMLDivElement> _this;

	public SamplePreviewItemElement(HtmlContentBuilder<HTMLDivElement> aThis) {
		super(aThis);
		_this = aThis;}

	@Override
	public Model selection() {
		return null;
	}

	@Override
	public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Model> selectionChangeEventListener) {
		return null;
	}

	@Override
	public SamplePreviewItemElement that() {
		return null;
	}
}
