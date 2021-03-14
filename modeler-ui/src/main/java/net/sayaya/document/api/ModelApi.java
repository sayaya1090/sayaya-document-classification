package net.sayaya.document.api;

import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Model;
import net.sayaya.ui.event.HasValueChangeHandlers;

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
	public static class ModelEvent {
		private static EventSource listener;
		public static ModelEvent listen() {
			if(listener!=null) listener.close();
			ModelEvent instance = new ModelEvent();
			listener = instance.listener();
			return instance;
		}
		private HasValueChangeHandlers.ValueChangeEventListener<Model> createCallback;
		private HasValueChangeHandlers.ValueChangeEventListener<Model> updateCallback;
		private HasValueChangeHandlers.ValueChangeEventListener<Model> deleteCallback;
		public ModelEvent onCreate(HasValueChangeHandlers.ValueChangeEventListener<Model> callback) {
			createCallback = callback;
			return this;
		}
		public ModelEvent onUpdate(HasValueChangeHandlers.ValueChangeEventListener<Model> callback) {
			updateCallback = callback;
			return this;
		}
		public ModelEvent onDelete(HasValueChangeHandlers.ValueChangeEventListener<Model> callback) {
			deleteCallback = callback;
			return this;
		}
		private EventSource listener() {
			EventSource src = new EventSource(host + "/models/changes");
			src.addEventListener("CREATE", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Model model = (Model) JSON.parse(json);
				if(createCallback!=null) createCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, model));
			});
			src.addEventListener("UPDATE", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Model model = (Model) JSON.parse(json);
				if(updateCallback!=null) updateCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, model));
			});
			src.addEventListener("DELETE", evt->{
				String json = (String)Js.asPropertyMap(evt).get("data");
				Model model = (Model) JSON.parse(json);
				if(deleteCallback!=null) deleteCallback.handle(HasValueChangeHandlers.ValueChangeEvent.event(evt, model));
			});
			return src;
		}
	}

	public void learn() {

	}
}
