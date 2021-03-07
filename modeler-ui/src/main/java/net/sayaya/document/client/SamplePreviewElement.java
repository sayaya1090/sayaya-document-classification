package net.sayaya.document.client;

import elemental2.dom.*;
import net.sayaya.document.api.SampleApi;
import net.sayaya.document.data.Model;
import net.sayaya.ui.HTMLElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;
import org.jboss.elemento.InputBuilder;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;

import static org.jboss.elemento.Elements.*;

public class SamplePreviewElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewElement> {
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
	private final HtmlContentBuilder<HTMLDivElement> bag = div().css("bag");
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
		//fire(event);
	}
	public SamplePreviewElement model(@NotNull Optional<Model> model) {
		if(model.isPresent()) {
			contentState = ContentState.NOT_EMPTY;
			this.model = model.get();
			SampleApi.findSamples(this.model, samples-> {
				bag.element().innerHTML = "";
				if(samples!=null && samples.length > 0) Arrays.stream(samples).map(SamplePreviewItemElement::instance).forEach(bag::add);
				else contentState = ContentState.EMPTY;
				updateStyle();
			});
			SampleApi.listenCreateSample(this.model.name(), sample->{
				bag.add(SamplePreviewItemElement.instance(sample));
				contentState = ContentState.NOT_EMPTY;
				updateStyle();
			});
			SampleApi.listenProcessingSample(this.model.name(), sample->{

			});
			SampleApi.listenAnalyzedSample(this.model.name(), sample->{

			});
			SampleApi.listenDeleteSample(this.model.name(), sample->{

			});
		} else {
			bag.element().innerHTML = "";
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

	private enum InputState {
		NORMAL, OVER
	}
	private enum ContentState {
		CLOSE, EMPTY, NOT_EMPTY
	}
}
