package net.sayaya.document.api;

import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.Sample;
import net.sayaya.ui.event.HasValueChangeHandlers;

import static elemental2.core.Global.JSON;

@UtilityClass
public class SampleApi {
	private String host = "http://localhost";
	public Promise<Sample[]> findSamples(Model model) {
		return DomGlobal.fetch(new Request(host + "/models/" + model.name() + "/samples"))
						.then(Response::json)
						.then(r-> Promise.resolve((Sample[])r));
	}
	public void uploadSamples(Model model, FormData files) {
		DomGlobal.fetch(new Request(host + "/models/" + model.name() + "/samples",
									RequestInitBuilder.create().method(RequestMethod.POST).body(files).build()));
	}
	public void removeSamples(Sample[] samples) {
		for(Sample sample: samples) DomGlobal.fetch(new Request(host + "/models/" + sample.model() + "/samples/" + sample.id(),
																RequestInitBuilder.create().method(RequestMethod.DELETE).build()));
	}
	public static class SampleEvent {
		private static EventSource listener;
		private HasValueChangeHandlers.ValueChangeEventListener<Sample> createCallback;
		private HasValueChangeHandlers.ValueChangeEventListener<Sample> processingCallback;
		private HasValueChangeHandlers.ValueChangeEventListener<Sample> analyzedCallback;
		private HasValueChangeHandlers.ValueChangeEventListener<Sample> deleteCallback;
		public static SampleEvent listen(String model) {
			if (listener != null) listener.close();
			SampleEvent instance = new SampleEvent();
			listener = instance.listener(model);
			return instance;
		}
		public SampleEvent onCreate(HasValueChangeHandlers.ValueChangeEventListener<Sample> callback) {
			createCallback = callback;
			return this;
		}
		public SampleEvent onProcessing(HasValueChangeHandlers.ValueChangeEventListener<Sample> callback) {
			processingCallback = callback;
			return this;
		}
		public SampleEvent onAnalyzed(HasValueChangeHandlers.ValueChangeEventListener<Sample> callback) {
			analyzedCallback = callback;
			return this;
		}
		public SampleEvent onDelete(HasValueChangeHandlers.ValueChangeEventListener<Sample> callback) {
			deleteCallback = callback;
			return this;
		}
		private EventSource listener(String model) {
			EventSource src = new EventSource(host + "/models/" + model + "/samples/changes");
			src.addEventListener("CREATE", evt->{
				String json = (String) Js.asPropertyMap(evt).get("data");
				Sample sample = (Sample) JSON.parse(json);
				if(createCallback!=null) createCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, sample));
			});
			src.addEventListener("PROCESSING", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Sample sample = (Sample) JSON.parse(json);
				if(processingCallback!=null) processingCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, sample));
			});
			src.addEventListener("ANALYZED", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Sample sample = (Sample) JSON.parse(json);
				if(analyzedCallback!=null) analyzedCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, sample));
			});
			src.addEventListener("DELETE", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Sample sample = (Sample) JSON.parse(json);
				if(deleteCallback!=null) deleteCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, sample));
			});
			return src;
		}
	}
}
