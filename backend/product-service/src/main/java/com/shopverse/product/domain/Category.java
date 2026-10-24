package com.shopverse.product.domain;


import jakarta.persistence.*;

@Entity
@Table(name="categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    protected Category(){

    }

    public Category(String name, String slug){
        this.name = name;
        this.slug = slug;
    }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public String getSlug(){ return slug; }
    public Category getParent(){ return parent; }

}
