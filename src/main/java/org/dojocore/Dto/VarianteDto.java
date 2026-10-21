package org.dojocore.Dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record VarianteDto (
        Integer id,
        Integer idProducto,
        String producto,
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal precio,
        Boolean activo,
        Set<Integer> idsValores,
        List<String> atributos,
        Integer stock){
}
