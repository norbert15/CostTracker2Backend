package hu.bnorbi.costtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "records")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;

    @NotNull
    @Min(-1000000000)
    @Max(1000000000)
    private Long value;

    private String comment;

    @NotBlank
    private String month;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Date created;
}
