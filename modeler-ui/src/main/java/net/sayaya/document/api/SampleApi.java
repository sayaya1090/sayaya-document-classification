package net.sayaya.document.api;

import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.RestApi;
import net.sayaya.document.data.Sample;

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
	private Callback<Sample> createCallback;
	private Callback<Sample> processingCallback;
	private Callback<Sample> analyzedCallback;
	private Callback<Sample> deleteCallback;
	public void listenCreateSample(String model, Callback<Sample> callback) {
		createCallback = callback;
		if(listener!=null) listener.close();
		listener = listener(model);
	}
	public void listenProcessingSample(String model, Callback<Sample> callback) {
		processingCallback = callback;
		if(listener!=null) listener.close();
		listener = listener(model);
	}
	public void listenAnalyzedSample(String model, Callback<Sample> callback) {
		analyzedCallback = callback;
		if(listener!=null) listener.close();
		listener = listener(model);
	}
	public void listenDeleteSample(String model, Callback<Sample> callback) {
		deleteCallback = callback;
		if(listener!=null) listener.close();
		listener = listener(model);
	}
	private EventSource listener;
	public EventSource listener(String model) {
		EventSource src = new EventSource(host + "/models/" + model + "/samples/changes");
		src.addEventListener("CREATE", evt->{
			String json = (String) Js.asPropertyMap(evt).get("data");
			Sample sample = (Sample) JSON.parse(json);
			if(createCallback!=null) createCallback.onSuccess(sample);
		});
		src.addEventListener("PROCESSING", evt->{
			String json = (String)Js.asPropertyMap(evt).get("data");
			Sample sample = (Sample) JSON.parse(json);
			if(processingCallback!=null) processingCallback.onSuccess(sample);
		});
		src.addEventListener("ANALYZED", evt->{
			String json = (String)Js.asPropertyMap(evt).get("data");
			Sample sample = (Sample) JSON.parse(json);
			if(analyzedCallback!=null) analyzedCallback.onSuccess(sample);
		});
		src.addEventListener("DELETE", evt->{
			DomGlobal.console.log(deleteCallback);
			String json = (String)Js.asPropertyMap(evt).get("data");
			Sample sample = (Sample) JSON.parse(json);
			if(deleteCallback!=null) deleteCallback.onSuccess(sample);
		});
		return src;
	}
}
