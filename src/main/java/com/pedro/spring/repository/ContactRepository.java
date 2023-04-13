package com.pedro.spring.repository;

import com.pedro.spring.domain.Contact;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c where login.id = :id")
    Page<Contact> findAllByIdLogin(@Param("id") UUID id, Pageable pageable);

    @Query("SELECT c FROM Contact c where login.id = :id")
    List<Contact> findAllByIdLogin(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM Contact c where c.login.id = :id")
    void deleteAllContactByLogin(@Param("id") UUID id);
}
