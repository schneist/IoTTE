# Introduction 


This project  is an example how to use the tagless final technique to build
 a set of operations that control an arduino like device using johnny-five-scalajs.


# Contents

This projects is a scala-js cross project, where the definition of the elements is done in the shared project,
and only the js specific parts are done in the js part.  

## Elements

The Parts of the operations that define the programatic structure of the outer combination of elements e.g. wait for an event on the device.

## Interpreter

Converts the user-program into a data structure that can be executes using a runner. E.g build a Task or Stream.

## Definitions

The parts that specify the inner semantics of the operations: specify the event is the pressing of a button on the device 

## Marshalling

Wrapping of inputs and outputs like in https://doc.akka.io/docs/akka-http/current/common/marshalling.html

## Runner

Interface that needs to be implemented in order to run the dsl programs.


# Demo

LED Button Demo contains an example where an Arduino with a Button on pin 2 and a LED at pin 10 wil make the LED blink, 
where the frequency of the blink is determined by the length of a buttonpress.