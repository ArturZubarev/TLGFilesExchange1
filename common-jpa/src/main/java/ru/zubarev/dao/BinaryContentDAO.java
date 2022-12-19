package ru.zubarev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zubarev.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent,Long> {
}
