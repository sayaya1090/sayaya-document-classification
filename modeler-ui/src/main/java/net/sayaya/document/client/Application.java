package net.sayaya.document.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import net.sayaya.ui.Button;
import net.sayaya.ui.Icon;
import org.jboss.elemento.Elements;

import static org.jboss.elemento.Elements.div;


public class Application implements EntryPoint {
	private final ControllerElement elemController = ControllerElement.instance();
	private final ModelGridElement elemModelGrid = ModelGridElement.instance();
	private final SamplePreviewElement elemSamplePreview = SamplePreviewElement.instance();
	private final Button btnNewModel = Button.outline().css("button").text("New Model").before(Icon.icon("add_circle"));
	private final Button btnDeleteModel = Button.outline().css("button").text("Delete").before(Icon.icon("delete"));
	private final Button btnSaveModel = Button.outline().css("button").text("Save").before(Icon.icon("save"));;
	@Override
	public void onModuleLoad() {
		btnNewModel.onClick(evt->createModel());
		btnDeleteModel.onClick(evt->deleteModel());
		btnSaveModel.onClick(evt->saveModel());
		Elements.body().add(div().css("top")
				.add(elemController.add(btnDeleteModel).add(btnNewModel).add(btnSaveModel))
				.add(div().css("layout")
						.add(elemModelGrid.css("layout-item"))
						.add(elemSamplePreview.css("layout-item"))));
	}
	private void createModel() {
		String name = DomGlobal.prompt("Input model name:");
	}
	private void deleteModel() {

	}
	private void saveModel() {

	}
}
