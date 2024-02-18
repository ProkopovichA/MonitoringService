package com.prokopovich.service;

import com.prokopovich.model.Audit;
import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.AuditRepo;
import com.prokopovich.repo.UsersRepo;
import com.prokopovich.repo.impl.AuditRepoImpl;
import com.prokopovich.repo.impl.UserRepoImpl;

import java.time.LocalDate;
import java.util.Optional;

public class UserService {
    public static Optional<User> singUp(String newLogin, String newPassword) {
        UsersRepo usersList = UserRepoImpl.getInstance();
        AuditRepo auditRepo = AuditRepoImpl.getInstance();

        User user = new User(777, newLogin, newPassword, UserRole.USER);
        if (usersList.addUser(user)) {
            OutputHandler.sout("Пользователь успешно зарегистрирован, теперь войдите в систему");
            auditRepo.addAudit(new Audit(user, LocalDate.now(), AuditAction.SIGN_UP));
        } else {
            OutputHandler.sout("Ошибка регистрации пользователя");
            user = null;
        }
        return Optional.of(user);
    }

    public static Optional<User> singIn(User user, String password) {
        AuditRepo auditRepo = AuditRepoImpl.getInstance();
        if (user.checkLoginPassword(user.getLogin(), password)) {
            OutputHandler.sout("Вы успешно вошли в систему.");
            auditRepo.addAudit(new Audit(user, LocalDate.now(), AuditAction.SIGN_IN));
            return Optional.of(user);
        } else {
            OutputHandler.sout("Не верный пароль");
            return Optional.empty();
        }
    }
}