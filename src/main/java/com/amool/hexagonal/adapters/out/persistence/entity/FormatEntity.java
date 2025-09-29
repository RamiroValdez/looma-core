<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/FormatEntity.java
package com.amool.hexagonal.adapters.out.persistence.entity;
========
package com.amool.hexagonal.adapters.out.persistence;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Format.java

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "format")
public class FormatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Relations

    // Categoría compone formatos
    @ManyToMany
<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/FormatEntity.java
    @JoinTable(name = "category_format", joinColumns = @JoinColumn(name = "format_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories = new HashSet<>();
========
    @JoinTable(name = "comp_format", joinColumns = @JoinColumn(name = "format_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Format.java

}
