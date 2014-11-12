Graph
=====

Create and traverse a graph network by reading data from an SQL Server.

This code reads a set of arcs from an SQL Server database and places it into an adjacency array data structure that allows to easily find the arcs into and out of each node. 

The graph traverses backward (arcs into a node) and forward (arcs out of a node).

The dataset is from the US Census and US Geological Survey for the town of Arlington, Massachusetts. The fields used are StartNode (node number at start of arc), EndNode (node number at end of arc), and Distance (length of arc in meters). Optionally, the street namecan be used to display the network output. 
