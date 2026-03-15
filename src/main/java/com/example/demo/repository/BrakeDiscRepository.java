package com.example.demo.repository;

import com.example.demo.entity.BrakeDisc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrakeDiscRepository extends JpaRepository<BrakeDisc, Long> {

    List<BrakeDisc> findByCodeContaining(String code);

    @Query("SELECT b FROM BrakeDisc b WHERE " +
           "b.code LIKE %:keyword% OR " +
           "b.oe1 LIKE %:keyword% OR " +
           "b.oe2 LIKE %:keyword% OR " +
           "b.oe3 LIKE %:keyword% OR " +
           "b.oe4 LIKE %:keyword% OR " +
           "b.oe5 LIKE %:keyword% OR " +
           "b.oe6 LIKE %:keyword% OR " +
           "b.oe7 LIKE %:keyword% OR " +
           "b.oe8 LIKE %:keyword% OR " +
           "b.oe9 LIKE %:keyword% OR " +
           "b.oe10 LIKE %:keyword% OR " +
           "b.oe11 LIKE %:keyword% OR " +
           "b.ek25 LIKE %:keyword% OR " +
           "b.ek1 LIKE %:keyword%")
    List<BrakeDisc> search(@Param("keyword") String keyword);
}
