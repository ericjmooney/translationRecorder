package org.wycliffeassociates.translationrecorder.persistence.mapping

import org.wycliffeassociates.translationrecorder.data.model.Anthology
import org.wycliffeassociates.translationrecorder.persistence.entity.AnthologyEntity

/**
 * Created by sarabiaj on 4/3/2018.
 */

class AnthologyMapper : Mapper<AnthologyEntity, Anthology> {
    override fun mapToEntity(type: Anthology): AnthologyEntity {
        return AnthologyEntity(
                type.id,
                type.slug,
                type.name,
                type.resource,
                type.regex,
                type.groups,
                type.mask,
                type.pluginJarName,
                type.pluginClassName
        )
    }

    override fun mapFromEntity(type: AnthologyEntity): Anthology {
        return Anthology(
                type.id,
                type.slug,
                type.name,
                type.resource,
                type.regex,
                type.groups,
                type.mask,
                type.pluginJarName,
                type.pluginClassName
        )
    }
}