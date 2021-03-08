package net.sayaya.document.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import elemental2.dom.DomGlobal;
import net.sayaya.document.api.ModelApi;
import net.sayaya.document.api.SampleApi;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.Button;
import net.sayaya.ui.Icon;
import org.jboss.elemento.Elements;

import static org.jboss.elemento.Elements.div;


public class Application implements EntryPoint {
	private final ControllerElement elemController = ControllerElement.instance();
	private final ModelGridElement elemModelGrid = ModelGridElement.instance();
	private final SamplePreviewElement elemSamplePreview = SamplePreviewElement.instance();
	private final Button btnNewModel = Button.outline().css("button").text("New Model").before(Icon.icon("add_circle"));
	private final Button btnDeleteModel = Button.outline().css("button").text("Delete Model").before(Icon.icon("delete"));
	private final Button btnSaveModel = Button.outline().css("button").text("Save Model").before(Icon.icon("save"));
	private final Button btnLearnModel = Button.outline().css("button").text("Learn").before(Icon.icon("smart_toy"));
	private final Button btnDeleteSample = Button.outline().css("button").text("Delete Sample").before(Icon.icon("delete")).style("display: none;");
	@Override
	public void onModuleLoad() {
		btnNewModel.onClick(evt->createModel());
		btnDeleteModel.onClick(evt->deleteModel());
		btnSaveModel.onClick(evt->saveModel());
		Elements.body().add(div().css("top")
				.add(elemController.add(div().add(btnSaveModel).add(btnNewModel).add(btnLearnModel).add(btnDeleteModel))
						.add(div().add(btnDeleteSample)))
				.add(div().css("layout")
						.add(elemModelGrid.css("layout-item"))
						.add(elemSamplePreview.css("layout-item"))));
		elemModelGrid.onSelectionChange(evt->{
			btnDeleteSample.element().style.display = null;
			btnDeleteSample.enabled(false);
			elemSamplePreview.model(evt.selection());
			Scheduler.get().scheduleFixedDelay(()->{
				elemModelGrid.refresh();
				return false;
			}, 200);
		});
		elemSamplePreview.onSelectionChange(evt->{
			if(evt.selection()==null || evt.selection().length <= 0) btnDeleteSample.enabled(false);
			else btnDeleteSample.enabled(true);
		});
		btnDeleteSample.onClick(evt->{
			Sample[] samples = elemSamplePreview.selection();
			if(samples == null || samples.length <= 0) return;
			else if(!DomGlobal.confirm("Delete " + samples.length + " sample(s). Continue?")) return;
			SampleApi.removeSamples(samples);
		});
		update();
		ModelApi.listenCreateModel(elemModelGrid::append);
		ModelApi.listenDeleteModel(elemModelGrid::delete);
	}
	private void update() {
		ModelApi.findModels(elemModelGrid::value);
	}
	private void createModel() {
		String name = DomGlobal.prompt("Input model name:");
		ModelApi.createModel(name);
	}
	private void deleteModel() {
		elemModelGrid.selection().map(Model::name).ifPresent(ModelApi::removeModel);
	}
	private void saveModel() {

	}
}
