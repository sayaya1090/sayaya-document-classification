package net.sayaya.document.modeler.sample

import org.mapstruct.Mapper
import org.mapstruct.NullValueMappingStrategy
import org.mapstruct.ReportingPolicy
import java.util.*

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
interface SampleToDTO {
    fun toSample(entity: Sample): net.sayaya.document.data.Sample
    @JvmDefault fun map(id: UUID): String {
        return id.toString()
    }
}