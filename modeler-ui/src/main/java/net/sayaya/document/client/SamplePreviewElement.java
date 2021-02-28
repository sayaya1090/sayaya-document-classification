package net.sayaya.document.client;

import elemental2.dom.*;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;
import org.jboss.elemento.InputBuilder;

import static org.jboss.elemento.Elements.*;

public class SamplePreviewElement extends HTMLElementBuilder<HTMLDivElement, SamplePreviewElement> {
	public static SamplePreviewElement instance() {
		return new SamplePreviewElement(div());
	}
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private final HTMLElement empty = div().style("text-align: center; align-self: center; width: 100%;")
			.add(i().css("material-icons").style("font-size: 6rem; color: rgb(221, 221, 221);").textContent("cloud_upload"))
			.add(h(3).style("text-align: center;color: rgb(221, 221, 221);font-size: 2.92rem;line-height: 110%;margin: 1.9466666667rem 0 1.168rem 0;").textContent("Drop file here"))
			.element();
	private final InputBuilder<HTMLInputElement> input = input("file").name("files");
	private final HTMLFormElement form = form().style("display: none;").add(input).element();
	private SamplePreviewElement(HtmlContentBuilder<HTMLDivElement> e) {
		super(e);
		_this = e.css("preview").add(empty).add(form);
		e.on(EventType.dragover, evt->{
			evt.stopPropagation();
			evt.preventDefault();
		});
		e.on(EventType.drop, evt->{
			evt.stopPropagation();
			evt.preventDefault();
			evt.stopImmediatePropagation();
			drop(evt);
		});
	}
	private void drop(DragEvent event) {
		input.element().files = event.dataTransfer.files;
		_this.element().innerHTML = "";
		// for(File f: input.element().files.asList()) _this.add(SamplePreviewItemElement.build(f));
		//fire(event);
	}
	/*private void fire(DragEvent event) {
		HasValueChangeHandlers.ValueChangeEvent<HTMLFormElement> evt = HasValueChangeHandlers.ValueChangeEvent.event(event, value());
		for(HasValueChangeHandlers.ValueChangeEventListener<HTMLFormElement> listener: listeners) listener.handle(evt);
	}*/
	@Override
	public SamplePreviewElement that() {
		return this;
	}
}
