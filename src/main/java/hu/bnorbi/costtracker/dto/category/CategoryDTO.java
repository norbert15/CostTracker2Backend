package hu.bnorbi.costtracker.dto.category;

import hu.bnorbi.costtracker.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    @NotBlank(message = "A név megadása kötelező!")
    @Column(unique = true)
    private String name;

    @NotBlank(message = "A szín megadása kötelező!")
    private String color;

    @NotBlank(message = "Az ikon megadása kötelező!")
    private String icon;

    @NotNull(message = "A tipus megadása kötelező!")
    private Long type;

    public Category toEntity() {
        Category category = new Category();
        category.setName(name);
        category.setColor(color);
        category.setIcon(icon);
        category.setType(type);

        return category;
    }
}
