package org.delcom.ifs25015springbootcashflow.usecase;

import static org.junit.jupiter.api.Assertions.*;

import org.delcom.ifs25015springbootcashflow.adapter.repository.UserRepository;
import org.delcom.ifs25015springbootcashflow.domain.PasswordHasher;
import org.delcom.ifs25015springbootcashflow.domain.entity.Role;
import org.delcom.ifs25015springbootcashflow.domain.entity.User;
import org.delcom.ifs25015springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class UseCaseGapTest {

    @Test
    void ensureRootAdminRepairsRoleOnly() throws Exception {
        UserRepository repo = new UserRepository();
        AuthUseCase auth = new AuthUseCase(repo);
        repo.save("root", PasswordHasher.hash("rahasia1"), "Root", Role.USER);

        User fixed = auth.ensureRootAdmin("root", "rahasia1");
        assertEquals(Role.ADMIN, fixed.role());
        assertEquals(Role.ADMIN, repo.findByUsername("root").orElseThrow().role());

        // sudah ADMIN dan password cocok -> tidak ada perubahan
        assertEquals(fixed, auth.ensureRootAdmin("root", "rahasia1"));
    }

    @Test
    void ensureRootAdminRepairsRoleAndPassword() throws Exception {
        UserRepository repo = new UserRepository();
        AuthUseCase auth = new AuthUseCase(repo);
        repo.save("boss", PasswordHasher.hash("lama123"), "Boss", Role.USER);

        User both = auth.ensureRootAdmin("boss", "baru1234");
        assertEquals(Role.ADMIN, both.role());
        assertEquals(PasswordHasher.hash("baru1234"), both.passwordHash());
    }

    @Test
    void updateProfileRejectsNullFullName() throws Exception {
        UserRepository repo = new UserRepository();
        User u = repo.save("budi", PasswordHasher.hash("rahasia1"), "Budi", Role.USER);
        UserUseCase uc = new UserUseCase(repo);
        assertThrows(ValidationException.class, () -> uc.updateProfile(u.id(), null, null));
    }
}
