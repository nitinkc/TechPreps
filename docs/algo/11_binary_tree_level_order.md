Note: This file is the full write-up for the binary tree level order problem; see `answers/coding_questions.md` for the consolidated index.
# 11. Binary Tree Level Order Traversal

## Learning Targets
- Use BFS with queue to traverse tree by levels.
- Handle null nodes gracefully.
- Analyze complexity.

## Problem
Return values per level for tree root = [3,9,20,null,null,15,7]. Output: [[3],[9,20],[15,7]].

## Approach (BFS)
Queue initialized with root. While not empty:
- size = current queue size (nodes in this level)
- For size times: poll node, append value, enqueue children if non-null.
- Append collected level list to result.

## Complexity
Let n = number of nodes.
- Time: O(n)
- Space: O(n) for queue (worst case last level ~ n/2).

## Java Implementation
```java
import java.util.*;
class TreeNode {int val; TreeNode left,right; TreeNode(int v){val=v;}}
public class LevelOrderTraversal {
    public List<List<Integer>> levelOrder(TreeNode root){
        List<List<Integer>> res = new ArrayList<>();
        if(root==null) return res;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        while(!q.isEmpty()){
            int sz = q.size();
            List<Integer> lvl = new ArrayList<>(sz);
            for(int i=0;i<sz;i++){
                TreeNode n = q.poll();
                lvl.add(n.val);
                if(n.left!=null) q.offer(n.left);
                if(n.right!=null) q.offer(n.right);
            }
            res.add(lvl);
        }
        return res;
    }
}
```

## Edge Cases
- Empty tree ⇒ [].
- Single node ⇒ [[value]].
- Skewed tree ⇒ each level has 1 node.

## Summary
Standard BFS grouping nodes by distance from root; queue ensures left-to-right order at each level.

