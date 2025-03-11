package app.dailyStatistics.repository;

import app.dailyStatistics.model.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, UUID> {
    Optional<DailyStatistics> findByDateAndUserId(LocalDate date, UUID userId);
}
