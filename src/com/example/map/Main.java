package com.example.map;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String [] args) {
        runMapTests();
    }

    private static void runMapTests() {
        MyMap<String, String> myMap = new MyMap<>();

        for (int i=1; i<=10; i++) {
            myMap.put(generateKey(i), generateValue(i));
        }

        test_containsKey(myMap);
        test_get(myMap);
        test_put();
        test_remove();
        test_put_and_remove_resizing();
        test_keySet(myMap);
        test_entrySet(myMap);
    }

    private static void test_containsKey(MyMap<String, String> myMap) {
        boolean validResponse = true;
        for (int i=1; i<=10; i++) {
            validResponse &= myMap.containsKey(generateKey(i));
        }
        print_test_result(validResponse, "containsKey() for existing keys");

        validResponse = !myMap.containsKey("some_random_key");
        print_test_result(validResponse, "containsKey() for non-existent keys");
    }

    // verify that map contains all the key-value pairs correctly
    private static void test_get(MyMap<String, String> myMap) {
        boolean validResponse = true;
        for (int i=1; i<=10; i++) {
            validResponse &= myMap.get(generateKey(i)).equals(generateValue(i));
        }
        print_test_result(validResponse, "get() for existing keys");

        // returns null for non-existent
        validResponse = myMap.get("some_random_key") == null;
        print_test_result(validResponse, "get() for non-existent");
    }

    private static void test_put() {
        MyMap<String, String> myMap = new MyMap<>();

        boolean validResponse = true;
        for (int i=1; i<=10; i++) {
            String previousValue = myMap.put(generateKey(i), generateValue(i));
            // as these are new keys being put in the map, the previous value
            // for all the keys should be null
            validResponse &= (previousValue == null);
        }
        print_test_result(validResponse, "put() for new keys");

        int existingKeyNumber = 5;

        // when we put value for an existing key, put returns the previous value
        String previousValue = myMap.put(generateKey(existingKeyNumber), "some_new_value");

        validResponse = previousValue.equals(generateValue(existingKeyNumber));
        print_test_result(validResponse, "put() for existing keys");
    }

    private static void test_remove() {
        MyMap<String, String> myMap = new MyMap<>();

        for (int i=1; i<=10; i++) {
            myMap.put(generateKey(i), generateValue(i));
        }

        boolean validResponse = true;
        for (int i=1; i<=10; i++) {
            boolean removed = myMap.remove(generateKey(i));
            // as these are new keys being put in the map, the previous value
            // for all the keys should be null
            validResponse &= removed;
        }
        print_test_result(validResponse, "remove() all values returned properly");

        boolean removed = myMap.remove("some_non_existent_key");
        validResponse = !removed;
        print_test_result(validResponse, "remove() non-existent key returns null");
    }

    private static void test_put_and_remove_resizing() {
        MyMap<String, String> myMap = new MyMap<>();

        for (int i=1; i<=8; i++) {
            myMap.put(generateKey(i), generateValue(i));
        }

        print_test_result(myMap.getBucketsCount() == 4, "put() no resizing happened initially");

        // put a new value for existing key - buckets should be the same
        myMap.put(generateKey(5), "new_value_for_existing_key");
        print_test_result(myMap.getBucketsCount() == 4, "put() no resizing happened due to replacing existing key");

        // adding a new Key-value - buckets should double
        myMap.put(generateKey(9), generateValue(9));

        print_test_result(myMap.getBucketsCount() == 8, "put() buckets count doubled with additional item");

        // removing the additional Key-value - buckets should be back to 4
        myMap.remove(generateKey(9));
        print_test_result(myMap.getBucketsCount() == 4, "remove() buckets count back to half with additional item removal");

        // removing all elements, the default buckets count (4) should be retained
        for(String key: myMap.keySet()) {
            myMap.remove(key);
        }

        print_test_result(myMap.getBucketsCount() == 4, "remove() default bucket count retained even when map is emptied");

    }

    private static void test_keySet(MyMap<String, String> myMap) {
        // keySet testing
        Set<String> keys = new HashSet<>();
        for (int i=1; i<=10; i++) {
            keys.add(generateKey(i));
        }
        boolean validResponse = myMap.keySet().equals(keys);
        print_test_result(validResponse, "keySet()");
    }

    private static void test_entrySet(MyMap<String, String> myMap) {
        Set<MyMap.Entry<String, String>> entries = new HashSet<>();
        for (int i=1; i<=10; i++) {
            entries.add(new MyMap.Entry<>(generateKey(i), generateValue(i)));
        }
        boolean validResponse = myMap.entrySet().equals(entries);

        print_test_result(validResponse, "entrySet()");
    }

    private static void print_test_result(boolean validResponse, String functionality) {
        if (validResponse) {
            System.out.println(functionality + " - assertion passed");
        }
        else {
            System.out.println(functionality + " - assertion FAILED!!");
        }
    }

    private static String generateKey(int number) {
        return "key_" + number;
    }

    private static String generateValue(int number) {
        return "value_" + number;
    }
}
