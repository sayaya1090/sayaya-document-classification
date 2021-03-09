package net.sayaya.document.api;

import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;

import static elemental2.core.Global.JSON;

@UtilityClass
public class ModelApi {
	private String host = "http://localhost";
	public Promise<Model[]> findModels() {
		Request request = new Request(host + "/models");
		return DomGlobal.fetch(request)
						.then(Response::json)
						.then(r->Promise.resolve((Model[])r));
	}
	public void createModel(String name) {
		Request request = new Request(host + "/models/" + name, RequestInitBuilder.create().method(RequestMethod.PUT).build());
		DomGlobal.fetch(request);
	}
	public void removeModel(String name) {
		Request request = new Request(host + "/models/" + name, RequestInitBuilder.create().method(RequestMethod.DELETE).build());
		DomGlobal.fetch(request);
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
	public void learn() {

	}
}
