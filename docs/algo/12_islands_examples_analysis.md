# 12. Islands Examples Analysis

## Overview
This document provides a detailed analysis of various island-counting examples using integer grid variants. The aim is to translate these grid variants into accurate island counts by confirming the adjacency rules, which specify that no diagonal connections are allowed. Through this analysis, we will also recognize patterns that emerge with isolated cells.

## Learning Targets
- Translate integer grid variants into island counts.
- Confirm adjacency rules (no diagonals).
- Recognize patterns of isolated cells.

## Grids & Expected Counts
1. All 1s:
```
1 1 1
1 1 1
1 1 1
```
**Answer:** 1 (single mass).

2. All 0s:
**Answer:** 0.

3. Checker corners:
```
1 0 1
0 1 0
1 0 1
```
Islands: Center (1), and four corner singles not connected horizontally/vertically.
**Total: 5.**

4. Mixed clusters:
```
1 0 0 1
0 0 0 0
0 1 1 0
0 0 0 0
```
Islands:
- (0,0)
- (0,3)
- Block (2,1) & (2,2)
**Total: 3.**

5. Large block top-left:
```
1 1 0 0
1 1 0 0
0 0 0 0
0 0 0 0
```
Connected 2x2 of ones ⇒ **1 island**.

## Approach Recap
Use DFS/BFS flood fill; each discovered '1' region increments count.

## Edge Case Notes
- Isolated single '1's count individually.
- Horizontal/vertical adjacency only; diagonals ignored even if touching at corners.

## Summary
Counting islands requires careful adjacency definition—diagonal patterns inflate isolated island count as seen in checker example.
