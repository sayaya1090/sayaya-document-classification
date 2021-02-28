package net.sayaya.document.data;

import jsinterop.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_= {@JsOverlay, @JsIgnore})
@Getter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent=true)
public final class Model {
	private String name;
	private Double cohesion;
	@JsProperty(name="cnt_documents")
	private Double cntDocuments;
	@JsOverlay
	@JsIgnore
	public Model cntDocuments(int cnt) {
		this.cntDocuments = cnt + 0.0;
		return this;
	}
	@JsOverlay
	@JsIgnore
	public int cntDocuments() {
		if(cntDocuments!=null) return cntDocuments.intValue();
		else return 0;
	}
}
