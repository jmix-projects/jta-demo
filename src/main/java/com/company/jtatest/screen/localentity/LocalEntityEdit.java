package com.company.jtatest.screen.localentity;

import io.jmix.ui.screen.*;
import com.company.jtatest.entity.LocalEntity;

@UiController("jtat_LocalEntity.edit")
@UiDescriptor("local-entity-edit.xml")
@EditedEntityContainer("localEntityDc")
public class LocalEntityEdit extends StandardEditor<LocalEntity> {
}