package ru.zubarev.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zubarev.dao.AppUserDAO;
import ru.zubarev.service.UserActivationService;
import ru.zubarev.utils.CryptoTool;
@Service
@AllArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;


    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
