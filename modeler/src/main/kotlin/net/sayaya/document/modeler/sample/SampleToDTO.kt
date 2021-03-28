package net.sayaya.document.modeler.sample

object SampleToDTO {
    fun map(entity: Sample): net.sayaya.document.data.Sample {
        return net.sayaya.document.data.Sample().id(entity.id.toString())
            .model(entity.model)
            .name(entity.name)
            .size(if(entity.size!=null) entity.size!! else 0)
            .page(if(entity.page!=null) entity.page!! else 0)
            .thumbnail(entity.thumbnail)
    }
}