package com.focusroot.forest;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ForestRepository extends JpaRepository<MyForest, Long> {

    List<MyForest> findByUserOrderByPlantedAtDesc(User user);

    long countByUserAndIsAlive(User user, Boolean isAlive);

    long countByUser_Id(Long userId);

    long countByUser_IdAndIsAlive(Long userId, Boolean isAlive);

    /**
     * Đếm số cây trồng mỗi ngày kể từ `from`, group by date.
     * cast(f.plantedAt as LocalDate) là Hibernate 6 JPQL syntax → dịch sang CAST(... AS DATE).
     */
    @Query("SELECT cast(f.plantedAt as LocalDate) AS date, COUNT(f) AS count " +
           "FROM MyForest f WHERE f.user.id = :userId AND f.plantedAt >= :from " +
           "GROUP BY cast(f.plantedAt as LocalDate)")
    List<DailyTreeCount> countTreesPerDaySince(@Param("userId") Long userId,
                                               @Param("from") LocalDateTime from);

    interface DailyTreeCount {
        LocalDate getDate();
        long getCount();
    }
}
