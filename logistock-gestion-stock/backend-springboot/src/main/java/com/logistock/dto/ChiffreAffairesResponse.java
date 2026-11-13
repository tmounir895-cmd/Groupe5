package com.logistock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChiffreAffairesResponse(LocalDate debut, LocalDate fin, long nombreVentes, BigDecimal total) {
}
