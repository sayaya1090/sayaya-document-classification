package net.sayaya.document.data;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_= {@JsOverlay, @JsIgnore})
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class Sample {
	private String id;
	private String model;
	private String name;
	private Double size;
	private Double page;
	private String thumbnail;

	@JsOverlay
	@JsIgnore
	public Sample size(long size) {
		this.size = size + 0.0;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public Long size() {
		if(this.size == null) return null;
		else return this.size.longValue();
	}

	@JsOverlay
	@JsIgnore
	public Sample page(int page) {
		this.page = page + 0.0;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public Integer page() {
		if(this.page == null) return null;
		else return this.page.intValue();
	}
}
