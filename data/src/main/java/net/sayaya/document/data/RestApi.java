package net.sayaya.document.data;

import jsinterop.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_={@JsOverlay, @JsIgnore})
@Getter(onMethod_={@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class RestApi {
	@JsProperty(name="request_id")
	private String requestId;
	private String method;
	@JsProperty(name="content_type")
	private String contentType;
	private String url;
	private String param;
	@JsProperty(name="file_name")
	private String fileName;
	@JsProperty(name="blob_type")
	private String blobType;
	@JsOverlay
	@JsIgnore
	public Method method() {
		if(method == null) return null;
		return Method.valueOf(method);
	}
	@JsOverlay
	@JsIgnore
	public RestApi method(Method method) {
		if(method == null) this.method = null;
		else this.method = method.name();
		return this;
	}
	public enum Method {
		GET,
		POST,
		PUT,
		DELETE,
		PATCH,
		HEAD,
		OPTIONS,
		TRACE
	}
}
