package hu.bnorbi.costtracker.repository;

import hu.bnorbi.costtracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.userId = 0 OR c.userId = ?1")
    List<Category> findAllByUserId(Long userId);

    @Query("SELECT c FROM Category c WHERE c.id = ?1 AND c.userId = ?2")
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT c.id FROM Category c WHERE c.type = ?1")
    List<Long> findIdsByType(Long type);

    @Query("SELECT c FROM Category c WHERE (c.userId = 0 OR c.userId = ?1) AND c.type = ?2")
    List<Category> findByTypeAndUserId(Long userId, Long type);
}
