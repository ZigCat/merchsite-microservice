package com.github.zigcat.merchsite_microservice.main.entity;

import com.github.zigcat.merchsite_microservice.main.entity.interfaces.Modelable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppOrder implements Modelable {
    @Id
    @SequenceGenerator(
            name = "orderSequence",
            sequenceName = "orderSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "orderSequence"
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    public AppOrder(AppUser user) {
        this.user = user;
    }
}
