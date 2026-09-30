package com.tecsup.util;

import com.tecsup.model.Role;
import com.tecsup.model.User;
import com.tecsup.repository.RoleRepository;
import com.tecsup.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(UserRepository userRepo,
                               RoleRepository roleRepo,
                               PasswordEncoder encoder) {
        return args -> {

            Role roleUser = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_USER")));

            Role roleAdmin = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));

            Role roleManager = roleRepo.findByName("ROLE_MANAGER")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_MANAGER")));

            // 2. Crear usuario USER (Nueva contraseña: user2026)
            User user = userRepo.findByUsername("user").orElse(new User());
            user.setUsername("user");
            user.setPassword(encoder.encode("user2026"));
            user.setRoles(Set.of(roleUser));
            userRepo.save(user);

            // 3. Crear usuario ADMIN (Nueva contraseña: admin2026)
            User admin = userRepo.findByUsername("admin").orElse(new User());
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin2026"));
            admin.setRoles(Set.of(roleAdmin));
            userRepo.save(admin);

            // 4. Crear/Actualizar usuario MANAGER (Nueva contraseña: manager2026)
            User manager = userRepo.findByUsername("manager").orElse(new User());
            manager.setUsername("manager");
            manager.setPassword(encoder.encode("manager2026"));
            manager.setRoles(Set.of(roleManager));
            userRepo.save(manager);

            System.out.println("✔ Datos iniciales cargados correctamente con nuevos roles y contraseñas");
        };
    }
}
