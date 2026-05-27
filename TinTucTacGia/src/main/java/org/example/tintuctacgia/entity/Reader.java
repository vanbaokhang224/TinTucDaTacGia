package org.example.tintuctacgia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "readers")
@DiscriminatorValue("READER")
public class Reader extends User {

    @Column(length = 500)
    private String bio;
}
