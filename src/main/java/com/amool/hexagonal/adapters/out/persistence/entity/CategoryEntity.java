<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/CategoryEntity.java
package com.amool.hexagonal.adapters.out.persistence.entity;
========
package com.amool.hexagonal.adapters.out.persistence;
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Category.java

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Relations

    // Obra tiene categorías
    @ManyToMany(mappedBy = "categories")
<<<<<<<< HEAD:src/main/java/com/amool/hexagonal/adapters/out/persistence/entity/CategoryEntity.java
    private Set<WorkEntity> workEntities = new HashSet<>();
========
    private Set<Work> works = new HashSet<>();

    public Set<Work> getWorks() {
        return works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }
>>>>>>>> origin/feature/mapeo_de_base_de_datos_sql:src/main/java/com/amool/hexagonal/adapters/out/persistence/Category.java

    // Formato compone categorías
    @ManyToMany(mappedBy = "categories")
    private Set<FormatEntity> formatEntities = new HashSet<>();

    // Usuario prefiere categorías
    @ManyToMany(mappedBy = "preferredCategories")
    private Set<UserEntity> usersWhoPrefer = new HashSet<>();

    public Set<UserEntity> getUsersWhoPrefer() {
        return usersWhoPrefer;
    }

    public void setUsersWhoPrefer(Set<UserEntity> usersWhoPrefer) {
        this.usersWhoPrefer = usersWhoPrefer;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

