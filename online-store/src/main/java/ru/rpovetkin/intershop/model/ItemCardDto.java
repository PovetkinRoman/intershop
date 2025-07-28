package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardDto implements Serializable {
    private Long id;
    private String imgPath;
    private String title;
    private BigDecimal price;
    private String description;
} 