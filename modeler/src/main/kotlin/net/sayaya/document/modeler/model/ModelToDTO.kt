package net.sayaya.document.modeler.model

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ModelToDTO {
    fun toModel(entity: Model): net.sayaya.document.data.Model
}