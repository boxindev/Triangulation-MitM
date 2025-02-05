# MitM Search on AES-EM

This folder contains the implementation of a MitM search. It uses the gurobi solver to compute truncated path.

## Compiling and running

A simple make will compile the code.

Before running, you may want to create a folder `output/` as the default output files will be saved in this folder and if it does not exist it will raise an error.

## Generation of tikz

There is a script to generate a tikz representation of the path. To compile, the only difference with the previous compilation is the command `make tikz`.

