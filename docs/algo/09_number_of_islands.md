Note: This file is the full write-up for the number of islands problem; see `answers/coding_questions.md` for the consolidated index.
# 9. Number of Islands (Grid of '1' and '0')

## Learning Targets
- Apply DFS/BFS flood fill.
- Track visited cells to avoid repetition.
- Compute time and space complexity.
- Discuss space usage nuance (call stack vs queue).

## Problem
Count connected components of land ('1') connected horizontally/vertically.

## Approach
Iterate all cells. When encountering '1' not visited, increment count and perform DFS/BFS to mark entire island.

## Complexity
Let m = rows, n = columns.
- Time: O(m*n) (each cell visited at most once).
- Space:
  - Visited array: O(m*n) if separate.
  - Or modify input grid: O(1) extra but destructive.
  - Recursion depth worst-case O(m*n) (single large island) → stack space.
BFS queue worst-case size O(min(m*n, perimeter of island)) ≤ O(m*n).

## Space Complexity Answer
If marking in-place and using iterative BFS:
- Auxiliary space: O(m*n) in worst-case for queue; typical answer: O(m*n) worst-case. If recursion: O(m*n) stack.

## Java Implementation (In-place DFS)
```java
public class NumIslands {
    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length, count = 0;
        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                if (grid[i][j]=='1') {
                    count++;
                    dfs(grid,i,j,m,n);
                }
            }
        }
        return count;
    }
    private void dfs(char[][] g,int r,int c,int m,int n){
        if(r<0||c<0||r>=m||c>=n||g[r][c]!='1') return;
        g[r][c]='0';
        dfs(g,r+1,c,m,n);
        dfs(g,r-1,c,m,n);
        dfs(g,r,c+1,m,n);
        dfs(g,r,c-1,m,n);
    }
}
```

## Edge Cases
- Empty grid ⇒ 0.
- All water ⇒ 0.
- All land ⇒ 1.

## Summary
Flood fill each discovered land cell to count islands. Space complexity O(m*n) considering recursion/queue; time O(m*n).

