//package com.example.donotredeem;
//
//import androidx.test.espresso.IdlingResource;
//import androidx.test.espresso.idling.CountingIdlingResource;
//
//public class FirestoreIdlingResource {
//    private static final String RESOURCE = "FIRESTORE";
//    private static final CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE);
//
//    public static void increment() {
//        countingIdlingResource.increment();
//    }
//
//    public static void decrement() {
//        if (!countingIdlingResource.isIdleNow()) {
//            countingIdlingResource.decrement();
//        }
//    }
//
//    public static IdlingResource getIdlingResource() {
//        return countingIdlingResource;
//    }
//}
