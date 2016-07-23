Toy Rx
======

This is a simplified implementation of Rx principles.
The main emphasis is made to implement change propagation to modified streams.

The implementation is not optimal and may suffer from memory leaks.

The goal of this project is to learn how Rx library works internally.

TODO
====

 - implement proper stream completion to wait for timed streams to terminate
 - implement merge, zip
 - implement timed modifiers (e.g. throttle, buffers, debounce)
 - implement simple UI app converting stream of clicks into stream of double clicks, long-clicks
 - implement simple spreadsheet calculator with dynamic subscription changes 
