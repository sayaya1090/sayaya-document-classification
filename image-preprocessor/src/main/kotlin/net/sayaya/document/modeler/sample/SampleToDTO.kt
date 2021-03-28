package net.sayaya.document.modeler.sample

object SampleToDTO {
    fun map(entity: Sample): net.sayaya.document.data.Sample {
        return net.sayaya.document.data.Sample().id(entity.id.toString())
            .model(entity.model)
            .name(entity.name)
            .size(entity.size)
            .page(entity.page)
            .thumbnail(entity.thumbnail)
    }
}