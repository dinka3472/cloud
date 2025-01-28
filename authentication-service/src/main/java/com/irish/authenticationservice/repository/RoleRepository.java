package com.irish.authenticationservice.repository;

import com.irish.authenticationservice.entities.Role;
import com.irish.authenticationservice.entities.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(" select r from Role r where r.name=:name ")
    Role getRoleByName(Enum<RoleNameEnum> name);
}
