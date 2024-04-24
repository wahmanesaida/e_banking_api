package com.ecommerce.api.Repository;

import com.ecommerce.api.Entity.Prospects;
import com.ecommerce.api.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProspectsRepository extends JpaRepository<Prospects, Long> {
    Prospects findByUser(User user);
}
