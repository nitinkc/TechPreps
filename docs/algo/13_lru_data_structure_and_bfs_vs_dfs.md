# 13. LRU Data Structure & BFS vs DFS

## Learning Targets
- Justify data structures for O(1) LRU operations.
- Compare BFS and DFS characteristics.
- Select traversal based on problem constraints.

## LRU Data Structure Choice
Use HashMap + Doubly Linked List.
- HashMap<K, Node>: O(1) key lookup for get/put.
- Doubly Linked List: maintain recency ordering; move node to head on access; remove tail for eviction in O(1).

Why Doubly?
- Need O(1) removal of arbitrary node; singly list would require previous pointer search (O(n)).
Why Not Just Array?
- Reordering or deletion in middle is O(n) shifting.
Why Not Queue Alone?
- Cannot remove a node from middle efficiently on get.

Alternatives
- LinkedHashMap (Java) internally combines Hash + doubly list; simpler API, but less explicit control.
- OrderedDict (Python) analogous.

## BFS vs DFS Trade-Offs (Graph/Tree Traversal)
| Aspect | BFS | DFS |
|--------|-----|-----|
| Order | Layer by layer | Depth path then backtrack |
| Shortest Path (Unweighted) | Guaranteed | Not guaranteed |
| Memory (Sparse Graph) | O(width) | O(depth) |
| Detect Cycle | Yes (with visited) | Yes (with visited) |
| Topological Sort | Can (Kahn's algorithm variant) | Can (post-order stack) |
| Early Target at Shallow Depth | Efficient | Might traverse deep first |
| Space Worst-Case | Large if level broad | Large if deep chain |

### When Prefer BFS
- Need shortest path in unweighted graph.
- Level-order processing (e.g., serialization, distance labeling).

### When Prefer DFS
- Explore entire component cheaply when depth limited.
- Detect cycles, classify edges, topological ordering.
- Backtracking problems (e.g., combinations, permutations).

### Complexity
Both visit each vertex/edge once: O(V+E). Space differs by pattern of graph.

## Summary
LRU demands O(1) node moves and eviction: HashMap + Doubly Linked List satisfies this. BFS vs DFS choice hinges on shortest path vs depth exploration and memory shape; both linear in total nodes/edges but differ in traversal order and resource profile.
