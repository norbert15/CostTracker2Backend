package hu.bnorbi.costtracker.repository;

import hu.bnorbi.costtracker.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT r FROM Record r WHERE r.userId = ?1 AND r.month = ?2")
    List<Record> findAllRecordByUserIdAndMonth(Long userId, String month);

    @Query("SELECT r FROM Record r WHERE r.userId = ?1 AND r.month = ?2 AND r.categoryId in ?3")
    List<Record> findAllRecordByUserIdAndMonthAndType(Long userId, String month, List<Long> categoryIds);

    @Query("SELECT r FROM Record r WHERE r.userId = ?1 AND r.month LIKE ?2%")
    List<Record> findAllRecordByUserIdAndYear(Long userId, Long year);

    @Query("SELECT r FROM Record r WHERE r.userId = ?1 AND r.month LIKE ?2% AND r.categoryId = ?3")
    List<Record> findAllByUserIdAndMonthAndCategoryId(Long userId, String month, Long categoryId);

    @Query("SELECT new Record(r.id AS id, r.userId AS userId, r.categoryId AS categoryId, SUM(r.value) AS value, " +
            "r.comment AS comment, r.month AS month, r.created AS created) " +
            "FROM Record r JOIN Category c ON r.categoryId = c.id " +
            "WHERE r.userId = ?1 " +
            "AND c.type = ?2 " +
            "AND r.month LIKE ?3% " +
            "GROUP BY r.month")
    List<Record> findAllByUserIdAndCategoryTypeAndYear(Long userId, Long type, Long year);
}
