package net.sayaya.document.modeler.model

object ModelToDTO {
    fun map(entity: Model): net.sayaya.document.data.Model {
        return net.sayaya.document.data.Model().name(entity.name).cohesion(entity.cohesion)
    }
}