package com.ibm.wala.core.tests.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.wala.util.graph.INodeWithNumberedEdges;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.impl.DelegatingNumberedGraph;
import com.ibm.wala.util.graph.traverse.FloydWarshall;
import com.ibm.wala.util.graph.traverse.FloydWarshall.GetPath;
import com.ibm.wala.util.graph.traverse.FloydWarshall.GetPaths;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.IntSetUtil;
import com.ibm.wala.util.intset.MutableIntSet;

public class FloydWarshallTest {

  public static class Node implements INodeWithNumberedEdges {
    private final int number;
    private final MutableIntSet preds = IntSetUtil.make();
    private final MutableIntSet succs = IntSetUtil.make();

    public int getGraphNodeId() {
      return number;
    }

    public Node(int number) {
      this.number = number;
    }

    public void setGraphNodeId(int number) {
      throw new UnsupportedOperationException();
    }

    public IntSet getSuccNumbers() {
      return succs;
    }

    public IntSet getPredNumbers() {
      return preds;
    }

    public void addSucc(int n) {
      succs.add(n);
    }

    public void addPred(int n) {
      preds.add(n);
    }

    public void removeAllIncidentEdges() {
      throw new UnsupportedOperationException();
    }

    public void removeIncomingEdges() {
      throw new UnsupportedOperationException();
    }

    public void removeOutgoingEdges() {
      throw new UnsupportedOperationException();
    }
    
    public String toString() {
      return "["+number+"]";
    }
  }
  
  private static NumberedGraph<Node> makeGraph() {
    NumberedGraph<Node> G = new DelegatingNumberedGraph<Node>();
  
    for(int i = 0; i <= 8; i++) {
      G.addNode(new Node(i));
    }
    
    G.addEdge(G.getNode(1),G.getNode(2));
    G.addEdge(G.getNode(2),G.getNode(3));
    G.addEdge(G.getNode(3),G.getNode(4));
    G.addEdge(G.getNode(3),G.getNode(5));
    G.addEdge(G.getNode(4),G.getNode(6));
    G.addEdge(G.getNode(5),G.getNode(7));
    G.addEdge(G.getNode(6),G.getNode(8));
    G.addEdge(G.getNode(7),G.getNode(8));

    G.addEdge(G.getNode(6),G.getNode(4));
    G.addEdge(G.getNode(6),G.getNode(2));

    return G;
  }
  
  private final NumberedGraph<Node> G = makeGraph();
  
  private final int[][] shortestPaths = new int[][]{
      {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, 
      {Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 2, 3, 3, 4, 4, 5},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 1, 2, 2, 3, 3, 4},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 4, 1, 1, 2, 2, 3},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 3, 2, 4, 1, 5, 2},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 2},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 2, 1, 3, 2, 4, 1},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1},
      {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}
  };
  
  @Test
  public void TestPathLengths() {
    int[][] result = FloydWarshall.shortestPathLengths(G);
    Assert.assertTrue(result.length == shortestPaths.length);
    for(int i = 0; i < result.length; i++) {
      Assert.assertTrue(result[i].length == shortestPaths[i].length);
      for(int j = 0; j < result[i].length; j++) {
        Assert.assertTrue(result[i][j] == shortestPaths[i][j]);
      }
    }
  }
  
  @Test
  public void TestShortestPath() {
    GetPath<Node> result = FloydWarshall.allPairsShortestPath(G);
    Assert.assertEquals(result.getPath(G.getNode(1), G.getNode(3)), Collections.singletonList(G.getNode(2)));
    Assert.assertEquals(result.getPath(G.getNode(5), G.getNode(8)), Collections.singletonList(G.getNode(7)));
    Assert.assertEquals(result.getPath(G.getNode(1), G.getNode(7)), Arrays.asList(G.getNode(2),G.getNode(3),G.getNode(5)));
    Assert.assertEquals(result.getPath(G.getNode(1), G.getNode(6)), Arrays.asList(G.getNode(2),G.getNode(3),G.getNode(4)));
  }

  @Test
  public void TestShortestPaths() {
    GetPaths<Node> result = FloydWarshall.allPairsShortestPaths(G);
    
    Set<List<Node>> paths = new HashSet<List<Node>>();
    paths.add(Arrays.asList(G.getNode(2),G.getNode(3),G.getNode(4),G.getNode(6)));
    paths.add(Arrays.asList(G.getNode(2),G.getNode(3),G.getNode(5),G.getNode(7)));    
    Assert.assertEquals(result.getPaths(G.getNode(1), G.getNode(8)), paths);
  }
}
