package com.company.jtatest.screen.localentity;

import io.jmix.ui.screen.*;
import com.company.jtatest.entity.LocalEntity;

@UiController("jtat_LocalEntity.browse")
@UiDescriptor("local-entity-browse.xml")
@LookupComponent("localEntitiesTable")
public class LocalEntityBrowse extends StandardLookup<LocalEntity> {
}