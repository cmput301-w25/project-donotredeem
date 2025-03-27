//package com.example.donotredeem;
//
//import static org.junit.Assert.assertEquals;
//
//import com.example.donotredeem.Classes.Users;
//import org.junit.Before;  // ✅ Use JUnit 4 annotation
//import org.junit.Test;
//
//public class UserProfileTests {
//    private Users users;
//
//    @Before  // Runs before each test
//    public void setUp() {
//        users = new Users("user1", "password", "test@gmail.com", "test instance");
//    }
//
//    @Test
//    public void testGettersAndSetters() {
//        users.setUsername("user12");
//        users.setPassword("password12");
//        users.setEmail("test1@gmail.com");
//        users.setBio("test instances");
//
//        assertEquals("user12", users.getUsername());
//        assertEquals("password12", users.getPassword());
//        assertEquals("test1@gmail.com", users.getEmail());
//        assertEquals("test instances", users.getBio());
//    }
//}
