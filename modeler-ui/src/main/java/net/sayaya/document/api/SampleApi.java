package net.sayaya.document.api;

import elemental2.dom.EventSource;
import elemental2.dom.FormData;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.RestApi;
import net.sayaya.document.data.Sample;

import static elemental2.core.Global.JSON;

@UtilityClass
public class SampleApi {
	private String host = "http://localhost";
	public void findSamples(Model model, Callback<Sample[]> callback) {
		RequestApi.request(new RestApi().method(RestApi.Method.GET).url(host + "/models/" + model.name() + "/samples"), response->{
			Sample[] result = (Sample[]) JSON.parse(response);
			// Slice<Worklist> result = (Slice<Worklist>) JSON.parse(json);
			callback.onSuccess(result);
			// ProgressApi.close();
		});
	}
	public void uploadSamples(Model model, FormData files) {
		RequestApi.request(new RestApi().method(RestApi.Method.POST).url(host + "/models/" + model.name() + "/samples"), files, response->{ });
	}
	public void removeDocument(String name, String document) {

	}
	private Callback<Sample> createCallback;
	private Callback<Sample> processingCallback;
	private Callback<Sample> analyzedCallback;
	private Callback<Sample> deleteCallback;
	public void listenCreateSample(String model, Callback<Sample> callback) {
		createCallback = callback;
		if(listener==null) listener = listener(model);
	}
	public void listenProcessingSample(String model, Callback<Sample> callback) {
		processingCallback = callback;
		if(listener==null) listener = listener(model);
	}
	public void listenAnalyzedSample(String model, Callback<Sample> callback) {
		analyzedCallback = callback;
		if(listener==null) listener = listener(model);
	}
	public void listenDeleteSample(String model, Callback<Sample> callback) {
		deleteCallback = callback;
		if(listener==null) listener = listener(model);
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
			String json = (String)Js.asPropertyMap(evt).get("data");
			Sample sample = (Sample) JSON.parse(json);
			if(deleteCallback!=null) deleteCallback.onSuccess(sample);
		});
		return src;
	}
}
