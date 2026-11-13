package com.logistock.dto;

import com.logistock.security.RoleNom;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(@NotNull(message = "Le rôle est obligatoire") RoleNom role) {
}
