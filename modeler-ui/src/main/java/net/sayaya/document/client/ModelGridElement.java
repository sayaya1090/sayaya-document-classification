package net.sayaya.document.client;

import elemental2.dom.HTMLDivElement;
import net.sayaya.document.data.Model;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.sheet.Data;
import net.sayaya.ui.sheet.Sheet;
import net.sayaya.ui.sheet.SheetSelectableSingle;
import net.sayaya.ui.sheet.column.ColumnBuilder;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.Arrays;
import java.util.Optional;

public final class ModelGridElement extends HTMLElementBuilder<HTMLDivElement, ModelGridElement> implements SheetSelectableSingle {
	public static ModelGridElement instance() {
		Sheet.SheetConfiguration config = Sheet.builder();
		return new ModelGridElement(config.build(), config);
	}
	private enum COLUMN_KEY {
		MODEL_NAME, COHESION, DOCUMENTS
	}
	private final Sheet sheet;
	private final Sheet.SheetConfiguration config;
	private ModelGridElement(Sheet sheet, Sheet.SheetConfiguration config) {
		super(sheet);
		this.sheet = sheet;
		this.config = config;
		layout();
	}
	private void layout() {
		sheet.style("border: 1px solid #ddd;");
		SheetSelectableSingle.header(sheet);
		config.columns(
				ColumnBuilder.string(COLUMN_KEY.MODEL_NAME.name()).width(400).name("Model").build(),
				ColumnBuilder.string(COLUMN_KEY.COHESION.name()).width(80).name("Cohesion").build(),
				ColumnBuilder.string(COLUMN_KEY.DOCUMENTS.name()).width(80).name("# of Docs").build()
		).stretchH("all");
	}
	public ModelGridElement value(Model... values) {
		sheet.values(Arrays.stream(values).map(ModelGridElement::map).toArray(Data[]::new));
		return that();
	}
	public ModelGridElement append(Model model) {
		sheet.append(map(model));
		return that();
	}
	public ModelGridElement delete(Model model) {
		sheet.delete(model.name()).refresh();
		return that();
	}
	private static Data map(Model value) {
		if(value == null) return null;
		return new Data(value.name())
				.put(COLUMN_KEY.MODEL_NAME.name(), value.name())
				.put(COLUMN_KEY.COHESION.name(), value.cohesion()!=null?String.valueOf(value.cohesion()):null)
				.put(COLUMN_KEY.DOCUMENTS.name(), String.valueOf(value.cntDocuments()));
	}
	@Override
	public Data[] value() {
		return sheet.values();
	}
	@Override
	public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Optional<Data>> selectionChangeEventListener) {
		return null;
	}
	@Override
	public ModelGridElement that() {
		return this;
	}
}
