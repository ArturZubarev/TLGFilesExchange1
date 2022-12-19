package ru.zubarev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zubarev.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument,Long> {
}
