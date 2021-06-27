package net.sayaya.document.client;

import elemental2.dom.*;
import net.sayaya.document.data.Model;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.event.HasSelectionChangeHandlers;
import net.sayaya.ui.sheet.Data;
import net.sayaya.ui.sheet.SheetElement;
import net.sayaya.ui.sheet.SheetElementSelectableSingle;
import net.sayaya.ui.sheet.column.ColumnBuilder;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;
import static org.jboss.elemento.EventType.bind;

public final class ModelGridElement extends HTMLElementBuilder<HTMLDivElement, ModelGridElement> implements HasSelectionChangeHandlers<Optional<Model>> {
	public static ModelGridElement instance() {
		return new ModelGridElement(div());
	}
	private enum COLUMN_KEY {
		MODEL_NAME, COHESION, DOCUMENTS
	}
	private final HtmlContentBuilder<HTMLLabelElement> lblEmpty = label("Model is not present yet. Create models.").style("text-align: center; align-self: center; width: 100%;");
	private final SheetElement sheet;
	private final SheetElement.SheetConfiguration config;
	private final HtmlContentBuilder<HTMLDivElement> _this;
	private final Map<String, Model> values = new HashMap<>();
	private ModelGridElement(HtmlContentBuilder<HTMLDivElement> e) {
		super(e.css("models"));
		_this = e;
		this.config = SheetElement.builder();
		this.sheet = config.build();

		layout();
	}
	private void layout() {
		SheetElementSelectableSingle.header(sheet);
		config.columns(
				ColumnBuilder.string(COLUMN_KEY.MODEL_NAME.name()).width(400).name("Model").build(),
				ColumnBuilder.string(COLUMN_KEY.COHESION.name()).width(80).name("Cohesion").build(),
				ColumnBuilder.string(COLUMN_KEY.DOCUMENTS.name()).width(80).name("# of Docs").build()
		).stretchH("all");
	}
	private void onUpdateSheet() {
		if(this.config.data()!=null && this.config.data().length > 0) {
			if(this.sheet.element().parentElement == null) {
				_this.element().textContent = "";
				_this.add(sheet);
			}
		} else {
			if(this.lblEmpty.element().parentElement == null) {
				_this.element().textContent = "";
				_this.add(lblEmpty);
			}
		}
	}
	public ModelGridElement value(Model... values) {
		this.values.clear();
		sheet.values(Arrays.stream(values)
				.peek(m->this.values.put(m.name(), m))
				.map(ModelGridElement::map)
				.toArray(Data[]::new));
		onUpdateSheet();
		return that();
	}
	public ModelGridElement append(Model model) {
		this.values.put(model.name(), model);
		sheet.append(map(model));
		onUpdateSheet();
		return that();
	}
	public ModelGridElement delete(Model model) {
		this.values.remove(model.name());
		sheet.delete(model.name());
		onUpdateSheet();
		return that();
	}
	private static Data map(Model value) {
		if(value == null) return null;
		return new Data(value.name())
				.put(COLUMN_KEY.MODEL_NAME.name(), value.name())
				.put(COLUMN_KEY.COHESION.name(), value.cohesion()!=null?String.valueOf(value.cohesion()):null)
				.put(COLUMN_KEY.DOCUMENTS.name(), String.valueOf(value.cntDocuments()));
	}
	public ModelGridElement refresh() {
		sheet.refresh();
		return that();
	}
	@Override
	public Optional<Model> selection() {
		return Arrays.stream(sheet.values())
				.filter((d) -> d.state() == Data.DataState.SELECTED)
				.map(d->values.get(d.idx()))
				.findAny();
	}
	@Override
	public HandlerRegistration onSelectionChange(EventTarget dom, SelectionChangeEventListener<Optional<Model>> listener) {
		EventListener wrapper = evt->listener.handle(SelectionChangeEvent.event(evt, selection()));
		return bind(dom, "selection-change", wrapper);
	}

	@Override
	public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Optional<Model>> listener) {
		return onSelectionChange(sheet.element(), listener);
	}
	@Override
	public ModelGridElement that() {
		return this;
	}
}
