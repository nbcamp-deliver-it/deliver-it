package com.sparta.deliverit.menu.domain.repository;

import com.sparta.deliverit.menu.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, String> {
}
