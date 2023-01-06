package ru.zubarev.services;

import ru.zubarev.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail (AppUser appUser,String email);
}
