package com.eldiamante360.deudor.presentation.mapper;

import com.eldiamante360.deudor.application.dto.AbonoResult;
import com.eldiamante360.deudor.presentation.dto.response.AbonoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbonoWebMapper {

    AbonoResponse toResponse(AbonoResult result);
}
