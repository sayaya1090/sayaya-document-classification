package net.sayaya.document.api;

import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import elemental2.dom.XMLHttpRequest;
import lombok.experimental.UtilityClass;
import net.sayaya.document.data.RestApi;

import java.util.Objects;

@UtilityClass
public class RequestApi {
	public void request(RestApi api, Callback<String> callback) {
		XMLHttpRequest request = new XMLHttpRequest();
		request.open(Objects.requireNonNull(api.method()).name(), api.url(), true);
		if(api.contentType()!=null) request.setRequestHeader("Content-Type", api.contentType());
		request.onreadystatechange = evt3->{
			if(request.readyState != XMLHttpRequest.DONE) return -1;
			else if(request.status == 200) {
				String response = request.responseText;
				if(request.getAllResponseHeaders().contains("X-Total-Count") && request.getResponseHeader("X-Total-Count")!=null) {
					String sb = "{" +
							"\"total_element\":" + request.getResponseHeader("X-Total-Count") + "," +
							"\"total_page\":" + request.getResponseHeader("X-Total-Page") + "," +
							"\"current_page\":" + request.getResponseHeader("X-Current-Page") + "," +
							"\"content\":" + response +
							"}";
					callback.onSuccess(sb);
				} else callback.onSuccess(response);
			} else callback.onFailure(new RuntimeException(request.responseText));
			return 0;
		};
		if(api.param()==null) request.send();
		else request.send(api.param());
	}
	public void download(RestApi api, Callback<Blob> callback) {
		XMLHttpRequest request = new XMLHttpRequest();
		request.open(Objects.requireNonNull(api.method()).name(), api.url(), true);
		if(api.contentType()!=null) request.setRequestHeader("Content-Type", api.contentType());
		request.responseType = "blob";
		request.onreadystatechange = evt3->{
			if(request.readyState != XMLHttpRequest.DONE) return -1;
			else if(request.status == 200) callback.onSuccess(blob(api.blobType(), request.response));
			else callback.onFailure(new RuntimeException(request.responseText));
			return 0;
		};
		if(api.param()==null) request.send();
		else request.send(api.param());
	}
	private Blob blob(String type, XMLHttpRequest.ResponseUnionType response) {
		BlobPropertyBag property = BlobPropertyBag.create();
		property.setType(type);
		return new Blob(new Blob.ConstructorBlobPartsArrayUnionType[]{
			Blob.ConstructorBlobPartsArrayUnionType.of(response)
		}, property);
	}
}
