# 2. LRU Cache (O(1) Operations)

## Learning Targets
- State LRU eviction policy.
- Choose proper data structures (HashMap + Doubly Linked List).
- Implement O(1) get and put.
- Explain time/space complexity and trade-offs.

## Concept
Least Recently Used (LRU): When capacity exceeded, remove the key not accessed for longest time.
Operations needed:
- get(key): return value or -1; mark key as most recently used.
- put(key,value): insert/update; move to most recent; evict if over capacity.

## Data Structures
1. HashMap<Integer, Node> for O(1) lookup by key.
2. Doubly Linked List of nodes ordered by recency (head = most recent, tail = least recent):
   - Remove node in O(1) with direct pointers.
   - Add node to front in O(1).

Alternative structures (e.g., LinkedHashMap) can simplify but explicit implementation shows understanding.

## Complexity
- get / put: O(1) average.
- Space: O(capacity).

## Edge Cases
- Capacity = 0 ⇒ all puts create immediate evictions (often disallowed; validate input).
- Updating existing key should not increase size.
- Frequent evictions ensure tail updated correctly.

## Java Implementation
```java
public class LRUCache {
    private static class Node {
        int key, value; Node prev, next;
        Node(int k,int v){key=k;value=v;}
    }
    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head; // dummy head
    private final Node tail; // dummy tail

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(-1,-1);
        tail = new Node(-1,-1);
        head.next = tail; tail.prev = head;
    }

    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        moveToFront(n);
        return n.value;
    }

    public void put(int key, int value) {
        Node n = map.get(key);
        if (n != null) {
            n.value = value;
            moveToFront(n);
            return;
        }
        if (map.size() == capacity) {
            evictLeastRecent();
        }
        Node fresh = new Node(key, value);
        insertFront(fresh);
        map.put(key, fresh);
    }

    private void moveToFront(Node n) {
        removeNode(n);
        insertFront(n);
    }

    private void insertFront(Node n) {
        n.next = head.next; n.prev = head;
        head.next.prev = n; head.next = n;
    }

    private void removeNode(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void evictLeastRecent() {
        Node lru = tail.prev;
        removeNode(lru);
        map.remove(lru.key);
    }
}
```

## Why Doubly Linked List + HashMap?
- Singly list cannot remove middle node in O(1) (need previous pointer).
- ArrayList removal in middle is O(n).
- HashMap alone cannot track recency order.

## Trade-Offs vs LinkedHashMap
- LinkedHashMap has built-in access-order iteration and removeEldestEntry hook—simpler but hides details.
- Custom implementation gives control (e.g., thread-safety wrap, instrumentation).

## Potential Enhancements
- Add size() and remove(key).
- Optional TTL per entry → would need ordered structure by expiry.
- Concurrency: wrap with synchronized or use ConcurrentHashMap + custom locking around list.

## Summary
Use HashMap for key→node and doubly linked list for recency ordering. Maintain most recent at front; evict from tail. Ensures O(1) operations.
