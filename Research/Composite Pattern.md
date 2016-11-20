# Composite Pattern

Composite design patten allows you to have a tree structure and ask each node in the tree structure to perform a task.You can take real life example of a organization.It have general managers and under general managers, there can be managers and  under managers there can be developers.Now you can set a tree structure and ask each node to perform common operation like getSalary().

As described by Gof:

"Compose objects into tree structure to represent part-whole hierarchies.Composite lets client treat individual objects and compositions of objects uniformly".

Composite design pattern treats each node in two ways-Composite or leaf.Composite means it can have other objects below it.leaf means it has no objects below it.