package net.sayaya.document.client;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.CheckBoxElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasStateChangeHandlers;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

public class SamplePreviewItemElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewItemElement> implements HasStateChangeHandlers<SamplePreviewItemElement.PreviewItemState> {
	public static SamplePreviewItemElement instance(Sample sample) {
		return new SamplePreviewItemElement(div()).sample(sample);
	}
	private final CheckBoxElement iptSelect = CheckBoxElement.checkBox(false).css("select");
	private final HtmlContentBuilder<HTMLDivElement> preview = div();
	private final PageElement page = new PageElement();
	private final HTMLLabelElement lblFileName = label().element();
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private final Set<StateChangeEventListener<PreviewItemState>> listeners = new HashSet<>();
	private Sample sample;
	private PreviewItemState state;
	private SamplePreviewItemElement(HtmlContentBuilder<HTMLDivElement> e) {
		super(e.css("preview-item"));
		_this = e;
		layout();
		state = PreviewItemState.LOADING;
		onStateChange(evt->draw());
		iptSelect.onValueChange(evt->{
			state = evt.value()?PreviewItemState.SELECTED:PreviewItemState.LOADED;
			fireStateChangeEvent();
		});
	}
	private void layout() {
		_this.add(iptSelect).add(preview.add(page)).add(lblFileName);
	}
	public SamplePreviewItemElement sample(Sample sample) {
		this.sample = sample;
		lblFileName.innerHTML = sample.name();
		fireStateChangeEvent();
		return that();
	}
	public Sample sample() {
		return sample;
	}
	private void draw() {
		if(state == PreviewItemState.LOADED) {
			iptSelect.element().style.display = "";
			css("loaded");
			ncss("uploading", "loading", "selected");
			page.thumbnail(sample.thumbnail());
		} else if(state == PreviewItemState.SELECTED) {
			iptSelect.element().style.display = "";
			css("selected");
			ncss("loaded", "uploading", "loading");
		} else if(state == PreviewItemState.LOADING) {
			iptSelect.element().style.display = "none";
			css("loading");
			ncss("loaded", "uploading", "selected");
			page.print("Processing...");
		} else if(state == PreviewItemState.UPLOADING) {
			iptSelect.element().style.display = "none";
			css("uploading");
			ncss("loaded", "loading", "selected");
			page.print("Uploading...");
		}
	}
	public SamplePreviewItemElement state(PreviewItemState state) {
		this.state = state;
		fireStateChangeEvent();
		return that();
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
		private PageElement thumbnail(String thumbnail) {
			this.element.innerHTML = null;
			this.element.style.backgroundImage = "url(data:image/png;base64," + thumbnail + ")";
			return that();
		}
		private PageElement print(String text) {
			this.element.innerHTML = text;
			this.element.style.background = null;
			return that();
		}
	}
}
