package net.sayaya.document.client;

import elemental2.dom.File;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.CheckBox;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class SamplePreviewItemElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewItemElement> implements HasStateChangeHandlers<SamplePreviewItemElement.PreviewItemState> {
	public static SamplePreviewItemElement instance(File file) {
		SamplePreviewItemElement elem = new SamplePreviewItemElement(div(), file.name);
		elem.state = PreviewItemState.UPLOADING;
		return elem;
	}
	public static SamplePreviewItemElement instance(Sample sample) {
		return new SamplePreviewItemElement(div(), sample.name()).sample(sample);
	}
	private final CheckBox iptSelect = CheckBox.checkBox(false).css("select");
	private final HtmlContentBuilder<HTMLDivElement> preview = div();
	private final PageElement page = new PageElement();
	private final HtmlContentBuilder<HTMLLabelElement> name = label();
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private final Set<StateChangeEventListener<PreviewItemState>> listeners = new HashSet<>();
	private Sample sample;
	private PreviewItemState state;
	private SamplePreviewItemElement(HtmlContentBuilder<HTMLDivElement> e, String fileName) {
		super(e.css("preview-item"));
		_this = e;
		layout();
		name.add(fileName);
		state = PreviewItemState.LOADING;
		onStateChange(evt->draw());
		iptSelect.onValueChange(evt->{
			state = evt.value()?PreviewItemState.SELECTED:PreviewItemState.LOADED;
			fireStateChangeEvent();
		});
	}
	private void layout() {
		_this.add(iptSelect).add(preview.add(page)).add(name);
	}
	private SamplePreviewItemElement sample(Sample sample) {
		this.sample = sample;
		state = PreviewItemState.LOADED;
		fireStateChangeEvent();
		return that();
	}
	public Sample sample() {
		return sample;
	}
	private void draw() {
		if(state == PreviewItemState.LOADED) {
			iptSelect.element().style.display = "";
			page.style("");
		} else if(state == PreviewItemState.SELECTED) {
			iptSelect.element().style.display = "";
			page.style("box-shadow: rgba(0, 0, 0, 0.23) 3px 3px 6px;");
		} else if(state == PreviewItemState.LOADING) {
			iptSelect.element().style.display = "none";
			page.style("");
		} else if(state == PreviewItemState.UPLOADING) {
			iptSelect.element().style.display = "none";
			page.style("");
		}
	}

	@Override
	public SamplePreviewItemElement that() {
		return this;
	}

	@Override
	public Collection<StateChangeEventListener<PreviewItemState>> listeners() {
		return listeners;
	}

	@Override
	public PreviewItemState state() {
		return state;
	}

	enum PreviewItemState {
		LOADING, LOADED, UPLOADING, SELECTED
	}
	private final static class PageElement extends HTMLElementBuilder<HTMLDivElement, PageElement> {
		public PageElement() {
			super(div().css("page"));
		}

		@Override
		public PageElement that() {
			return this;
		}
	}
}
