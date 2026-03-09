# 1. Shortest Path in a Weighted Graph (Example Graph)

## Learning Targets
After this, you should be able to:
- Model a small weighted graph.
- Explain and apply Dijkstra’s algorithm.
- Trace algorithm state (distances, visited set, priority queue) step by step.
- Justify time and space complexity.
- Know when BFS is insufficient (weighted edges).

## Problem
Find the shortest path between two nodes (A to D) in the weighted graph:
```
A --2-- B
|       / \
1     3    1
|   /      \
C --------- D
      4
```
Treat edges as undirected (unless otherwise specified):
- A–B (2), A–C (1)
- B–C (3), B–D (1)
- C–D (4)

Goal example: shortest path A → D.

## Why Not BFS?
BFS only guarantees shortest path in *unweighted* (or uniform weight) graphs. Here weights differ, 
so we need a weighted shortest path algorithm: Dijkstra (weights are positive).

## Dijkstra’s Algorithm (High-Level)
1. Initialize distance to source = 0; others = ∞.
2. Use a min-priority queue keyed by current best distance.
3. Repeatedly extract node u with smallest tentative distance.
4. Relax each edge (u,v,w): if dist[u] + w < dist[v], update dist[v] and predecessor[v] and push into PQ.
5. Stop when target popped (optional optimization) or PQ empty.

## Step-by-Step Walkthrough (Source A)
Initial:
- dist(A)=0, dist(B)=∞, dist(C)=∞, dist(D)=∞
- PQ: (A,0)

Pop A:
- Relax A→B: dist(B)=2 (prev[B]=A)
- Relax A→C: dist(C)=1 (prev[C]=A)
PQ: (C,1), (B,2)

Pop C (1):
- C→B: 1+3=4 ≥ 2 (ignore)
- C→D: dist(D)=1+4=5 (prev[D]=C)
PQ: (B,2), (D,5)

Pop B (2):
- B→D: 2+1=3 < 5 ⇒ dist(D)=3 (prev[D]=B)
PQ: (D,3), (D,5)  (second D entry stale)

Pop D (3): target reached with optimal distance 3. (Can terminate.)

## Path Reconstruction
Backtrack with predecessors:
- D → B → A. Reverse: A → B → D. Cost = 2 + 1 = 3.
Alternative path A→C→D = 1 + 4 = 5 (longer).

## Complexity
Let V = number of vertices, E = number of edges.
- Time: O((V + E) log V) using binary heap PQ.
- Space: O(V + E) adjacency + O(V) dist + O(V) PQ + O(V) predecessor.

## Edge Cases / Notes
- Disconnected: unreachable nodes retain ∞ distance.
- Multiple shortest paths: predecessor selection may differ; all have same distance.
- Negative weights: Dijkstra invalid; need Bellman-Ford (O(VE)).
- Directed graphs: just store directed adjacency; logic same.

## Java Example
```
A --2-- B
|       / \
1     3    1
|   /      \
C --------- D
      4
```
```java
import java.util.*;

public class DijkstraExample {
    static class Edge {int to, w; Edge(int t,int w){to=t;this.w=w;}}

    static List<List<Edge>> buildGraph() {
        // Node mapping: A=0, B=1, C=2, D=3
        List<List<Edge>> g = new ArrayList<>();
        for(int i=0;i<4;i++) g.add(new ArrayList<>());
        addUndirected(g,0,1,2); // A 0 - B 1 -> weight 2
        addUndirected(g,0,2,1); // A-C
        addUndirected(g,1,2,3); // B-C
        addUndirected(g,1,3,1); // B-D
        addUndirected(g,2,3,4); // C-D
        return g;
    }
    static void addUndirected(List<List<Edge>> g,int u,int v,int w){
        g.get(u).add(new Edge(v,w));
        g.get(v).add(new Edge(u,w));
    }
    static List<Integer> shortestPath(int src,int tgt,List<List<Edge>> g){
        int n = g.size();//Total nodes/vertices
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);//Infinity
        Arrays.fill(prev, -1);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        pq.offer(new int[]{src,0});
        boolean[] visited = new boolean[n];
        while(!pq.isEmpty()){
            int[] cur = pq.poll();
            int u = cur[0];
            if(visited[u]) continue;
            visited[u] = true;
            if(u == tgt) break; // early exit
            for(Edge e: g.get(u)){
                if(!visited[e.to] && dist[u] + e.w < dist[e.to]){
                    dist[e.to] = dist[u] + e.w;
                    prev[e.to] = u;
                    pq.offer(new int[]{e.to, dist[e.to]});
                }
            }
        }
        if(dist[tgt] == Integer.MAX_VALUE) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        for(int at = tgt; at != -1; at = prev[at]) path.addFirst(at);
        return path; // indices representing path
    }

    public static void main(String[] args){
        List<List<Edge>> g = buildGraph();
        List<Integer> path = shortestPath(0,3,g); // A->D
        System.out.println("Path indices: " + path); // [0,1,3]
    }
}
```

## Verification Tips
- Ensure PQ may hold multiple entries for same node; use visited[] to skip stale ones.
- Early exit when target popped is safe (Dijkstra property).

## Summary
Dijkstra’s algorithm incrementally finalizes shortest distances by always expanding the node with minimal tentative distance. For the sample graph, A→B→D is optimal with total weight 3.
