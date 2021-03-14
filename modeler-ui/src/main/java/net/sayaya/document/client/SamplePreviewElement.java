package net.sayaya.document.client;

import elemental2.dom.*;
import net.sayaya.document.api.SampleApi;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;
import org.jboss.elemento.InputBuilder;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.jboss.elemento.Elements.*;

public class SamplePreviewElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewElement> implements HasSelectionChangeHandlers<Sample[]> {
	public static SamplePreviewElement instance() {
		return new SamplePreviewElement(div());
	}
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private final HTMLElement empty = div().css("empty-label")
			.add(i().css("material-icons").style("font-size: 6rem;transform: translate(-50%, calc(-50% - 3rem));").textContent("cloud_upload"))
			.add(h(3).style(
					"text-align: center;font-size: 2.92rem; line-height: 110%;margin: 1.9466666667rem 0 1.168rem 0;white-space: nowrap;" +
					"transform: translate(-50%, calc(-50% - 6rem));").textContent("Drop file here"))
			.element();
	private final InputBuilder<HTMLInputElement> input = input("file").name("files");
	private final HTMLFormElement form = form().style("display: none;").add(input).element();
	private final SamplePreviewItemBagElement bag = new SamplePreviewItemBagElement(div());
	private ContentState contentState = ContentState.CLOSE;
	private InputState inputState = InputState.NORMAL;
	private Model model;
	private SamplePreviewElement(HtmlContentBuilder<HTMLDivElement> e) {
		super(e);
		_this = e.css("preview").add(bag).add(empty).add(form);
		e.on(EventType.dragleave, evt->{
			evt.stopPropagation();
			evt.preventDefault();
			inputState = InputState.NORMAL;
			updateStyle();
		});
		e.on(EventType.dragover, evt-> {
			evt.stopPropagation();
			evt.preventDefault();
			inputState = InputState.OVER;
			updateStyle();
		});
		e.on(EventType.drop, evt->{
			evt.stopPropagation();
			evt.preventDefault();
			evt.stopImmediatePropagation();
			drop(evt);
			inputState = InputState.NORMAL;
			updateStyle();
		});
		updateStyle();
	}
	private void drop(DragEvent event) {
		input.element().files = event.dataTransfer.files;
		SampleApi.uploadSamples(model, new FormData(form));
		updateStyle();
	}
	public SamplePreviewElement model(@NotNull Optional<Model> model) {
		if(model.isPresent()) {
			contentState = ContentState.NOT_EMPTY;
			this.model = model.get();
			SampleApi.findSamples(this.model).then(samples->{
				bag.clear();
				if(samples!=null && samples.length > 0) Arrays.stream(samples).map(SamplePreviewItemElement::instance)
						.peek(e->e.state(SamplePreviewItemElement.PreviewItemState.LOADED))
						.forEach(bag::add);
				else contentState = ContentState.EMPTY;
				updateStyle();
				return null;
			});
			SampleApi.SampleEvent.listen(this.model.name()).onCreate(evt->{
				bag.add(SamplePreviewItemElement.instance(evt.value()).state(SamplePreviewItemElement.PreviewItemState.UPLOADING));
				contentState = ContentState.NOT_EMPTY;
				updateStyle();
			}).onProcessing(evt->{
				bag.get(evt.value().id()).ifPresent(elem->elem.sample(evt.value()).state(SamplePreviewItemElement.PreviewItemState.LOADING));
			}).onAnalyzed(evt->{
				bag.get(evt.value().id()).ifPresent(elem->elem.sample(evt.value()).state(SamplePreviewItemElement.PreviewItemState.LOADED));
			}).onDelete(evt->{
				bag.remove(evt.value());
				if(bag.isEmpty()) contentState = ContentState.EMPTY;
				else contentState = ContentState.NOT_EMPTY;
				updateStyle();
			});
		} else {
			bag.clear();
			this.model = null;
			contentState = ContentState.CLOSE;
			updateStyle();
		}
		return that();
	}
	private void updateStyle() {
		switch(inputState) {
			case NORMAL:    ncss("preview-over");break;
			case OVER:      css("preview-over"); break;
		}
		switch(contentState) {
			case CLOSE:     css("preview-closed"); ncss("preview-empty"); break;
			case EMPTY:     ncss("preview-closed"); css("preview-empty"); break;
			case NOT_EMPTY: ncss("preview-closed", "preview-empty"); break;
		}
	}
	@Override
	public SamplePreviewElement that() {
		return this;
	}

	@Override
	public Sample[] selection() {
		return bag.selection();
	}

	@Override
	public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Sample[]> selectionChangeEventListener) {
		return bag.onSelectionChange(selectionChangeEventListener);
	}

	private enum InputState {
		NORMAL, OVER
	}
	private enum ContentState {
		CLOSE, EMPTY, NOT_EMPTY
	}
	private final static class SamplePreviewItemBagElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewItemBagElement> implements HasSelectionChangeHandlers<Sample[]> {
		private final HtmlContentBuilder<HTMLDivElement> _this;
		private final Map<String, SamplePreviewItemElement> map = new HashMap<>();
		private final Set<SamplePreviewItemElement> selected = new HashSet<>();
		private SamplePreviewItemBagElement(HtmlContentBuilder<HTMLDivElement> e) {
			super(e.css("bag"));
			_this = e;
		}
		public SamplePreviewItemBagElement add(SamplePreviewItemElement child) {
			_this.add(child);
			child.onStateChange(evt->{
				if(evt.state() == SamplePreviewItemElement.PreviewItemState.SELECTED) selected.add(child);
				else selected.remove(child);
				this.element().dispatchEvent(new CustomEvent("change"));
			});
			map.put(child.sample().id(), child);
			return that();
		}
		public Optional<SamplePreviewItemElement> get(String id) {
			return Optional.ofNullable(map.get(id));
		}
		public SamplePreviewItemBagElement remove(Sample smp) {
			if(map.containsKey(smp.id())) {
				map.get(smp.id()).element().remove();
				map.remove(smp.id());
			}
			selected.removeIf(sel -> sel.sample().id().equals(smp.id()));
			return that();
		}
		public SamplePreviewItemBagElement clear() {
			element().innerHTML = "";
			map.clear();
			selected.clear();
			this.element().dispatchEvent(new CustomEvent("change"));
			return that();
		}
		public boolean isEmpty() {
			return map.isEmpty();
		}
		@Override
		public SamplePreviewItemBagElement that() {
			return this;
		}

		@Override
		public Sample[] selection() {
			return selected.stream().map(SamplePreviewItemElement::sample).toArray(Sample[]::new);
		}

		@Override
		public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Sample[]> selectionChangeEventListener) {
			return onSelectionChange(this.element, selectionChangeEventListener);
		}
	}
}
