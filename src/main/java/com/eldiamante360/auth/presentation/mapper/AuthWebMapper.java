package com.eldiamante360.auth.presentation.mapper;

import com.eldiamante360.auth.application.dto.LoginCommand;
import com.eldiamante360.auth.application.dto.RefreshTokenCommand;
import com.eldiamante360.auth.application.dto.SesionResult;
import com.eldiamante360.auth.presentation.dto.request.LoginRequest;
import com.eldiamante360.auth.presentation.dto.request.RefreshTokenRequest;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    @Mapping(target = "ipOrigen", source = "ip")
    LoginCommand toCommand(LoginRequest request, String ip, String userAgent);

    @Mapping(target = "refreshTokenPlano", source = "request.refreshToken")
    @Mapping(target = "ipOrigen", source = "ip")
    RefreshTokenCommand toCommand(RefreshTokenRequest request, String ip, String userAgent);

    TokenResponse toResponse(SesionResult result);
}
