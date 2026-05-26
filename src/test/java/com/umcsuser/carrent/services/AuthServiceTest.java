package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepositoryMock;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        authService = new AuthService(userRepositoryMock);
    }

    // TESTY LOGOWANIA

    @Test
    void shouldLoginAdminSuccessfully() {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User adminUser = new User("admin", hashedPassword, Role.ADMIN);

        when(userRepositoryMock.getUser("admin")).thenReturn(adminUser);

        User result = authService.login("admin", "admin123");

        assertNotNull(result, "Użytkownik powinien zostać poprawnie zalogowany");
        assertEquals("admin", result.getLogin(), "Login powinien się zgadzać");
        assertEquals(Role.ADMIN, result.getRole(), "Rola powinna być ADMIN");

        System.out.println("✅ TEST ZDANY: Logowanie na administratora (poprawne dane) działa bezbłędnie!");
    }

    @Test
    void shouldLoginUserWithCorrectPassword() {
        String hashedPassword = BCrypt.hashpw("tajne123", BCrypt.gensalt());
        User standardUser = new User("janek", hashedPassword, Role.USER);
        when(userRepositoryMock.getUser("janek")).thenReturn(standardUser);

        User result = authService.login("janek", "tajne123");

        assertNotNull(result, "Użytkownik powinien zostać zalogowany");
        assertEquals("janek", result.getLogin());
        assertEquals(Role.USER, result.getRole(), "Rola powinna być ustawiona na USER");

        System.out.println("✅ TEST ZDANY: Zalogowano istniejącego użytkownika używając poprawnego hasła!");
    }

    @Test
    void shouldNotLoginUserWithWrongPassword() {
        String hashedPassword = BCrypt.hashpw("tajne123", BCrypt.gensalt());
        User standardUser = new User("janek", hashedPassword, Role.USER);
        when(userRepositoryMock.getUser("janek")).thenReturn(standardUser);

        User result = authService.login("janek", "złe_hasło");

        assertNull(result, "Logowanie z błędnym hasłem powinno zwrócić null");

        System.out.println("✅ TEST ZDANY: Zablokowano próbę logowania z błędnym hasłem!");
    }

    @Test
    void shouldNotLoginNonExistentUser() {
        when(userRepositoryMock.getUser("duch")).thenReturn(null);

        User result = authService.login("duch", "jakies_haslo");

        assertNull(result, "Logowanie na nieistniejące konto powinno zwrócić null");

        System.out.println("✅ TEST ZDANY: Pomyślnie odrzucono próbę zalogowania na nieistniejący login!");
    }

    // TESTY REJESTRACJI

    @Test
    void shouldRegisterNewUserSuccessfully() {
        when(userRepositoryMock.addUser(eq("nowy_klient"), anyString())).thenReturn(true);

        boolean result = authService.register("nowy_klient", "bezpieczne_haslo");

        assertTrue(result, "Rejestracja nowego użytkownika powinna zakończyć się sukcesem (zwrócić true)");
        verify(userRepositoryMock, times(1)).addUser(eq("nowy_klient"), anyString());

        System.out.println("✅ TEST ZDANY: System pomyślnie zarejestrował nowego użytkownika!");
    }

    @Test
    void shouldNotRegisterUserWithTakenLogin() {
        User existingUser = new User("admin", "stare_haslo", Role.ADMIN);

        when(userRepositoryMock.getUser("admin")).thenReturn(existingUser);

        when(userRepositoryMock.addUser(eq("admin"), anyString())).thenReturn(false);

        boolean result = authService.register("admin", "moje_haslo");

        assertFalse(result, "Próba rejestracji na zajęty login musi zakończyć się niepowodzeniem (zwrócić false)");

        verify(userRepositoryMock, never()).addUser(anyString(), anyString());

        System.out.println("✅ TEST ZDANY: System poprawnie zablokował próbę utworzenia konta na już zajęty login!");
    }
}