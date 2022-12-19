package ru.zubarev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zubarev.entity.RawData;

public interface RawDataDao extends JpaRepository<RawData, Long> {
}
