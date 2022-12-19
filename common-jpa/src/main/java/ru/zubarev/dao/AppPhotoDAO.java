package ru.zubarev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zubarev.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto,Long> {
}
