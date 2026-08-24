package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.HallMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HallMenuRepository extends JpaRepository<HallMenu, UUID> {
    List<HallMenu> findByHall_Id(UUID hallId);
    List<HallMenu> findByMenu_Id(UUID menuId);
    void deleteByMenu_Id(UUID menuId);
}
