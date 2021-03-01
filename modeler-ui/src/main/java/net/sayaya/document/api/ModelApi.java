package net.sayaya.document.api;

import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventSource;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.RestApi;

import static elemental2.core.Global.JSON;

@UtilityClass
public class ModelApi {
	private String host = "http://localhost:12647";
	public void findModels(Callback<Model[]> callback) {
		// ProgressApi.open(true);
		RequestApi.request(new RestApi().method(RestApi.Method.GET).url(host + "/models"), response->{
			Model[] result = (Model[]) JSON.parse(response);
			// Slice<Worklist> result = (Slice<Worklist>) JSON.parse(json);
			callback.onSuccess(result);
			// ProgressApi.close();
		});
	}
	public void createModel(String name) {
		RequestApi.request(new RestApi().method(RestApi.Method.PUT).url(host + "/models/" + name), response->{ });
	}
	public void removeModel(String name) {
		RequestApi.request(new RestApi().method(RestApi.Method.DELETE).url(host + "/models/" + name), response->{});
	}
	private Callback<Model> createCallback;
	private Callback<Model> updateCallback;
	private Callback<Model> deleteCallback;
	public void listenCreateModel(Callback<Model> callback) {
		createCallback = callback;
		if(listener==null) listener = listener();
	}
	public void listenUpdateModel(Callback<Model> callback) {
		updateCallback = callback;
		if(listener==null) listener = listener();
	}
	public void listenDeleteModel(Callback<Model> callback) {
		deleteCallback = callback;
		if(listener==null) listener = listener();
	}
	private EventSource listener;
	public EventSource listener() {
		EventSource src = new EventSource(host + "/models/changes");
		src.addEventListener("CREATE", evt->{
			String json = (String)Js.asPropertyMap(evt).get("data");
			Model model = (Model) JSON.parse(json);
			if(createCallback!=null) createCallback.onSuccess(model);
		});
		src.addEventListener("UPDATE", evt->{
			String json = (String)Js.asPropertyMap(evt).get("data");
			Model model = (Model) JSON.parse(json);
			if(updateCallback!=null) updateCallback.onSuccess(model);
		});
		src.addEventListener("DELETE", evt->{
			String json = (String)Js.asPropertyMap(evt).get("data");
			Model model = (Model) JSON.parse(json);
			if(deleteCallback!=null) deleteCallback.onSuccess(model);
		});
		return src;
	}
	public void findDocuments(String model, Callback<Document> callback) {

	}
	public void uploadDocuments() {

	}
	public void removeDocument(String name, String document) {

	}
	public void learn() {

	}
}
