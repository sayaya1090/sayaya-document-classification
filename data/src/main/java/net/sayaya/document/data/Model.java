package net.sayaya.document.data;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType
@Setter(onMethod_= {@JsMethod})
@Getter(onMethod_= {@JsMethod})
@Accessors(fluent=true)
public final class Model {
	private String name;
	private Double cohesion;
	@JsProperty(name="cnt_documents")
	private Double cntDocuments;
}
