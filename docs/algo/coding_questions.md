# Consolidated Coding Questions & Solutions

This file is the canonical, consolidated index for the repository's coding interview problems. It provides a one-line problem description, a short solution summary, complexity notes, and links to the detailed write-up files located in this `answers/` directory.

Learning targets
- Read and understand common interview problems (linked below).
- Pick the right algorithm/data structure and explain trade-offs.
- Sketch a solution, give complexity, and refer to an implementation where available.

How to use
- Each entry contains a one-line problem, a short solution summary, complexity, and a link to the deep-dive file under this directory.

Contents

1. Shortest Path in Weighted Graph
   - Summary: Use Dijkstra for non-negative weights; reconstruct path with predecessors.
   - Complexity: O((V+E) log V)
   - Full write-up: `01_shortest_path_weighted_graph.md`

2. LRU Cache (O(1) operations)
   - Summary: HashMap + Doubly Linked List; evict tail.
   - Complexity: O(1) average get/put
   - Full implementation: `02_lru_cache.md` and design notes in `13_lru_data_structure_and_bfs_vs_dfs.md`

3. Reverse a Singly Linked List
   - Summary: Iterative pointer reversal (prev/curr/next); recursive alternative.
   - Complexity: O(n) time, O(1) space (iterative)
   - Full implementation: `03_reverse_linked_list.md`

4. Subarray Product Less Than k
   - Summary: Sliding window using multiplicative product; shrink left while product >= k.
   - Complexity: O(n)
   - Full explanation & code: `05_subarray_product_less_than_k.md`

5. Minimum Number of Machines (Interval Overlap)
   - Summary: Sweep line on start/end events to compute peak concurrent tasks.
   - Complexity: O(n log n) due to sorting events
   - Full write-up: `06_min_machines_for_tasks.md`

6. Minimum Cost to Connect Sticks
   - Summary: Greedy combine two smallest sticks repeatedly using min-heap (Huffman-like).
   - Complexity: O(n log n)
   - Full explanation: `07_min_cost_connect_sticks.md`

7. Task Dependency Scheduler (topological with parallel execution)
   - Summary: Dependency counting (in-degree), submit ready tasks to thread pool, handle failures and cancellation.
   - Complexity: O(n + e)
   - Full design: `08_task_dependency_scheduler.md` (not purely algorithmic but relevant)

8. Number of Islands
   - Summary: DFS/BFS flood fill; mark visited.
   - Complexity: O(R*C)
   - Full articles: `09_number_of_islands.md` and `12_islands_examples_analysis.md`

9. Combination Sum
   - Summary: Backtracking with pruning; allow reuse of candidates.
   - Complexity: Exponential worst-case
   - Full code: `10_combination_sum.md`

10. Binary Tree Level Order Traversal
    - Summary: BFS queue by level.
    - Complexity: O(n)
    - Full code: `11_binary_tree_level_order.md`

11. Poles sequence x, 2x, 4x
    - Summary: HashSet membership checks for x,2x,4x; O(n) time.
    - Complexity: O(n)
    - Full notes: `15_poles_sequence_x_2x_4x.md`

12. 90th Percentile Response Time
    - Summary: Sort & select nearest-rank or use t-digest for streaming approximation.
    - Complexity: O(n log n) for sort, O(n) average with quickselect.
    - Full write-up: `14_ninetieth_percentile_api_response.md`

13. LRU design & BFS vs DFS trade-offs (theory)
    - Summary: Data structures and traversal trade-offs summarized.
    - Full notes: `13_lru_data_structure_and_bfs_vs_dfs.md`

Additions & maintenance notes
- If you add a new coding answer file under `answers/`, also add a short entry here linking it so this file remains the single index.
- When updating an existing coding file, ensure this consolidated file’s one-line summary still matches the detailed content.

Credits
- Collated from the project's structured guide and individual `answers/` files.
