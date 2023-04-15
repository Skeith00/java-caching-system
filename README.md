# java-caching-system
Java Caching System

CacheEntry class to represent the cacheManager entries

CacheNode class to represent the cacheManager nodes. Each node will be responsible for a portion of the cacheManager entries, based on the hash value of the keys

Cache class to manage the cacheManager nodes and distribute the cacheManager entries among them

You can use the Cache class to put and get values from the cacheManager

In this example, we're using three cacheManager nodes, and the cacheManager entries are distributed among them based on the hash value of the keys. When we call get("key1"), for example, the getNode method calculates the hash value of the key and uses it to determine which cacheManager node is responsible for that key. The get method then retrieves the cacheManager entry from

https://medium.com/@sandeep4.verma/consistent-hashing-8eea3fb4a598
https://web.archive.org/web/20221230083731/https:/michaelnielsen.org/blog/consistent-hashing/
https://www.toptal.com/big-data/consistent-hashing
