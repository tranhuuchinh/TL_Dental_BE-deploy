package com.dreamtech.tldental.models;

import com.dreamtech.tldental.utils.Utils;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name="Policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    @Column(nullable = false)
    private String symbol;

    @Column(unique = true)
    private String slug;

    @Column(length = 5000)
    private String detail;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        this.slug = Utils.generateSlug(name);
    }

    @PreUpdate
    protected void preUpdate() {
        this.slug = Utils.generateSlug(name);
    }

    public Policy() {
    }

    public Policy(String name, String symbol, String detail) {
        this.name = name;
        this.symbol = symbol;
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", slug='" + slug + '\'' +
                ", detail='" + detail + '\'' +
                ", createAt=" + createAt +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
