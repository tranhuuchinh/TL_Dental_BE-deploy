package com.dreamtech.tldental.repositories;

import com.dreamtech.tldental.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, String>  {
    Policy findBySlug(String slug);
    List<Policy> findByName(String name);

}
