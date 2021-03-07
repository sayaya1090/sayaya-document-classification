package net.sayaya.document.api;

import elemental2.dom.File;
import elemental2.dom.FormData;
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
}
