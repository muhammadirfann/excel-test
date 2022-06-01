package com.example.map;

import java.util.*;

public class MyMap<K,V> {

    /**
     * Maximum number of nodes allowed per bucket
     *
     * With more data being added, we'll increase the number of buckets
     * to keep the size to buckets ratio under LOADING_FACTOR
     *
     */
    private static final double LOADING_FACTOR = 2.0;
    private static final int DEFAULT_INITIAL_BUCKETS = 4;

    /**
     * The factor by which we increase/decrease the number of
     * buckets, while adding/removing data
     */
    private static final int BUCKETS_COUNT_SCALING_FACTOR = 2;

    private LinkedList[] buckets;
    private int size;

    public MyMap() {
        initializeBuckets(DEFAULT_INITIAL_BUCKETS);
        this.size = 0;
    }

    /**
     * Stores the key-value pair in the map.
     *
     * The map starts with an initial capacity to store data, but has the ability to
     * expand dynamically as more data is added.
     *
     * Complexity:
     *      O(1) + O(LOADING_FACTOR), which is constant time, as the LOADING_FACTOR is constant
     *          O(1) is for getting the right bucket via the key hashcode
     *          O(LOADING_FACTOR) is for 'looping' over that bucket to find an existing entry, else append at the end
     * @param key - key for the entry
     * @param value - corresponding value in the entry
     * @return if the map has an existing entry for the given key, the old value
     * is returned, otherwise null is returned
     */
    public V put(K key, V value) {
        int bucketIndex = getBucketIndex(key);

        LinkedList<Entry<K,V>> targetList = (LinkedList<Entry<K,V>>) this.buckets[bucketIndex];

        V previousValue;

        Optional<Entry<K,V>> entry = findElementInBucket(bucketIndex, key);

        if (entry.isPresent()) {
            previousValue = entry.get().value;
            entry.get().value = value;

            return previousValue;
        }

        // No existing entry was found at this point
        // we'll append the entry to the end of the LinkedList
        targetList.add(new Entry<>(key, value));

        // update the size of the map
        this.size++;

        if (shouldDecreaseBuckets()) {
            rehash(true);
        }

        return null;  //  no previous value for the given key
    }

    /**
     * To check whether the given key already exists in the Map, even if
     * it's with a null as value
     *
     * Complexity:
     *       O(1) + O(LOADING_FACTOR), which is constant time, as the LOADING_FACTOR is constant
     *           O(1) is for getting the right bucket via the key hashcode
     *           O(LOADING_FACTOR) is for 'looping' over that bucket to find an existing entry
     * @param key the key to be looked up
     * @return true if key exists in the map, even if the value is null,
     * otherwise false
     */
    public boolean containsKey(K key) {
        int bucketIndex = getBucketIndex(key);
        Optional<Entry<K,V>> entryOptional = findElementInBucket(bucketIndex, key);

        return entryOptional.isPresent();
    }

    /**
     * Get the value for a given key
     *
     *  Complexity:
     *       O(1) + O(LOADING_FACTOR), which is constant time, as the LOADING_FACTOR is constant
     *           O(1) is for getting the right bucket via the key hashcode
     *           O(LOADING_FACTOR) is for 'looping' over that bucket to find an existing entry
     * @param key the key to look up
     * @return the corresponding value if the key exists, otherwise null
     */
    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        Optional<Entry<K,V>> entryOptional = findElementInBucket(bucketIndex, key);

        // This may also be a null, if null was inserted in the map for given key
        return entryOptional.map(entry -> entry.value).orElse(null);
    }

    /**
     * to remove the entry (key-value pair) from the map for the given key
     *
     * The underlying data-structure will shrink dynamically as the data is removed,
     * while ensuring a default minimum size
     *
     *  Complexity:
     *       O(1) + O(LOADING_FACTOR), which is constant time, as the LOADING_FACTOR is constant
     *           O(1) is for getting the right bucket via the key hashcode
     *           O(LOADING_FACTOR) is for 'looping' over that bucket to find an existing entry, else return null
     *
     * @param key - the key to search and remove the entry
     * @return true if an entry is present for this key, otherwise false
     */
    public boolean remove(K key) {
        int bucketIndex = getBucketIndex(key);

        LinkedList<Entry<K,V>> targetBucket = (LinkedList<Entry<K,V>>) this.buckets[bucketIndex];
        for(Entry<K,V> entry: targetBucket) {
            if(entry.key.equals(key)) {
                targetBucket.remove(entry);
                this.size--;

                if(shouldIncreaseBuckets()) {
                    rehash(false);
                }

                return true;
            }
        }

        // No value found if we're outside the above loop
        return false;
    }

    /**
     * Collects the set of all keys in the map
     *
     * Complexity:
     *      O(n) - need to loop over all the buckets, and contents of each, to collect keys
     * @return set of all keys
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for(LinkedList<Entry<K,V>> bucket: buckets) {
            for(Entry<K,V> entry: bucket) {
                keys.add(entry.key);
            }
        }

        return keys;
    }

    /**
     * Complexity:
     *       O(n) - need to loop over all the buckets, and contents of each, to collect all entries
     * @return Set of all entries (key-value pairs) in the map
     */
    public Set<Entry<K,V>> entrySet() {
        Set<Entry<K,V>> entries = new HashSet<>();
        for(LinkedList<Entry<K,V>> bucket: buckets) {
            for(Entry<K,V> entry: bucket) {
                entries.add(entry);
            }
        }

        return entries;
    }

    /**
     * This method is not a part of the usual Map public API.
     * I'm exposing this just to test out the shrinking and expansion
     * of the underlying data-structure while data is removed from the map
     * or added to it
     * @return current number of buckets to hold the data for the map
     */
    public int getBucketsCount() {
        return this.buckets.length;
    }

    private boolean shouldIncreaseBuckets() {
        // if half of the buckets can fit all the contents, but not less than default size
        return ( (double) this.size / (this.buckets.length) ) * 0.5 < LOADING_FACTOR
                    && this.getNewBucketsCountAfterShrink() >= DEFAULT_INITIAL_BUCKETS;
    }

    private boolean shouldDecreaseBuckets() {
        // if the current contents are causing this map to exceed its loading-factor
        return (double) this.size / this.buckets.length > LOADING_FACTOR;
    }

    private Optional<Entry<K,V>> findElementInBucket(int bucketIndex, K key) {
        LinkedList<Entry<K,V>> targetBucket = (LinkedList<Entry<K,V>>) this.buckets[bucketIndex];
        for(Entry<K,V> entry: targetBucket) {
            if(entry.key.equals(key)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % this.buckets.length;
    }

    private void rehash(boolean expand) {
        // get a pointer to the old bucket
        LinkedList [] oldBuckets = this.buckets;

        // initialize the buckets to double the previous size, or half the size
        int newBucketCount = expand ? this.getNewBucketsCountAfterExpand() :
                this.getNewBucketsCountAfterShrink();

        this.initializeBuckets(newBucketCount);
        this.size = 0;

        // Now we'll put the key-value pairs from the old buckets, to the
        // new larger/smaller array of buckets
        // Resizing will change the distribution of these entries to spread
        // over the new buckets
        for(LinkedList<Entry<K,V>> bucket: oldBuckets) {
            for(Entry<K,V> entry: bucket) {
                this.put(entry.key, entry.value);
            }
        }
    }

    private int getNewBucketsCountAfterShrink() {
        return this.buckets.length / BUCKETS_COUNT_SCALING_FACTOR;
    }

    private int getNewBucketsCountAfterExpand() {
        return this.buckets.length * BUCKETS_COUNT_SCALING_FACTOR;
    }

    private void initializeBuckets(int numBuckets) {
         this.buckets = new LinkedList[numBuckets];
         for(int i = 0; i < numBuckets; i++) {
             this.buckets[i] = new LinkedList();
         }
    }

    static class Entry<K,V> {
        private K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "key: " + key + ", value: " + value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
