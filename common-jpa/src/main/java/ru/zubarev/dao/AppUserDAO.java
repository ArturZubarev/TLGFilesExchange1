package zubarev.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import zubarev.entity.AppUser;

import javax.persistence.Column;
@Repository
public interface AppUserDAO extends JpaRepository<AppUser, Long>{
    AppUser findAPPUserByTelegramUserId(Long id);
}
