package net.sayaya.document.api;

import elemental2.core.ArrayBuffer;
import elemental2.core.ArrayBufferView;
import elemental2.core.JsArray;
import elemental2.dom.*;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
@Setter(onMethod_= {@JsOverlay, @JsIgnore})
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class RequestInitBuilder {
	@JsOverlay
	public static RequestInitBuilder create() {
		return Js.uncheckedCast(JsPropertyMap.of());
	}
	@JsOverlay
	@JsIgnore
	public elemental2.dom.RequestInit build() {
		return Js.uncheckedCast(this);
	}
	@JsProperty
	private elemental2.dom.RequestInit.GetBodyUnionType body;
	@JsProperty
	private String cache;
	@JsProperty
	private String credentials;
	@JsProperty
	private elemental2.dom.RequestInit.GetHeadersUnionType headers;
	@JsProperty
	private String integrity;
	@JsProperty
	private String method;
	@JsProperty
	private String mode;
	@JsProperty
	private String redirect;
	@JsProperty
	private String referrer;
	@JsProperty
	private String referrerPolicy;
	@JsProperty
	private AbortSignal signal;
	@JsProperty
	private Object window;
	@JsProperty
	private boolean keepalive;
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(ArrayBuffer body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(ArrayBufferView body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(Blob body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(FormData body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	public RequestInitBuilder body(elemental2.dom.RequestInit.GetBodyUnionType body) {
		this.body = body;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(ReadableStream body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(String body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder body(URLSearchParams body) {
		return body(Js.<elemental2.dom.RequestInit.GetBodyUnionType>uncheckedCast(body));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder cache(String cache) {
		this.cache = cache;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder credentials(String credentials) {
		this.credentials = credentials;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(elemental2.dom.RequestInit.GetHeadersUnionType headers) {
		this.headers = headers;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(Headers headers) {
		return headers(Js.<elemental2.dom.RequestInit.GetHeadersUnionType>uncheckedCast(headers));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(String key, String param) {
		Headers header = new Headers();
		header.append(key, param);
		return headers(header);
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(JsArray<JsArray<String>> headers) {
		return headers(Js.<elemental2.dom.RequestInit.GetHeadersUnionType>uncheckedCast(headers));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(JsPropertyMap<String> headers) {
		return headers(Js.<elemental2.dom.RequestInit.GetHeadersUnionType>uncheckedCast(headers));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder headers(String[][] headers) {
		return headers(Js.<JsArray<JsArray<String>>>uncheckedCast(headers));
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder integrity(String integrity) {
		this.integrity = integrity;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder keepalive(boolean keepalive) {
		this.keepalive = keepalive;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder method(RequestMethod method) {
		if(method == null) this.method = RequestMethod.GET.name();
		else this.method = method.name();
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder mode(String mode) {
		this.mode = mode;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder redirect(String redirect) {
		this.redirect = redirect;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder referrer(String referrer) {
		this.referrer = referrer;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder referrerPolicy(String referrerPolicy) {
		this.referrerPolicy = referrerPolicy;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder signal(AbortSignal signal) {
		this.signal = signal;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public RequestInitBuilder window(Object window) {
		this.window = window;
		return this;
	}
}