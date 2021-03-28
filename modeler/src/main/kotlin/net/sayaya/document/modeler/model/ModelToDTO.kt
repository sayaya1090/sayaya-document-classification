@file:JvmName("ModelToDTO")
package net.sayaya.document.modeler.model

fun map(entity: Model): net.sayaya.document.data.Model {
    return net.sayaya.document.data.Model().name(entity.name).cohesion(entity.cohesion)
}