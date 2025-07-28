package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("item")
public class Item {
    @Id
    private Long id;
    private String title;
    private String description;
    @Column(value = "img_path")
    private String imgPath;
    private Integer count;
    private BigDecimal price;

    public Item(String title, String description, String imgPath, Integer count, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.count = count;
        this.price = price;
    }
}
