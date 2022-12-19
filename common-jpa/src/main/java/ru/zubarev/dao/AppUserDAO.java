package ru.zubarev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zubarev.entity.AppUser;

@Repository
public interface AppUserDAO extends JpaRepository<AppUser, Long>{
    AppUser findAPPUserByTelegramUserId(Long id);
}
