package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        testUser.setRole(User.Role.ADMIN);
        testUser.setEnabled(true);
        userRepository.save(testUser);
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals(User.Role.ADMIN, found.get().getRole());
        assertTrue(found.get().isEnabled());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        newUser.setRole(User.Role.MANAGER);
        newUser.setEnabled(true);
        
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals(User.Role.MANAGER, saved.getRole());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(testUser);
        
        Optional<User> found = userRepository.findByUsername("testuser");
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testUpdateUser() {
        testUser.setRole(User.Role.VIEWER);
        User updated = userRepository.save(testUser);
        
        assertEquals(User.Role.VIEWER, updated.getRole());
    }
}
