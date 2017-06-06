# Ch3 Basic Data Types

Four types in go: basic types, aggregate types, reference types and interface types
Reference types: indirectly referred.

## Integer
integers: (u)int(8/16/32/64)
rune: unicode code point
byte: synonym for uint8
uintptr : sufficient to store bits of a pointer

## Float
float32 float64
math.MaxFloat32 math.MaxFloat64
float32: approximately 6 decimal digits of precision
%g %e %f can be used to print floats
+-Inf NaN math.IsNaN()

## string
attempt to access a string ouside the range will panic
ith byte of a string is not necessarily ith character of the string, because
UTF-8 encoding of a non-ASCII code point requires two or more bytes
s[i:j] will create a new string [i, j)
\+ will concatenate two strings
string values are immutable, += statement will create a new string, it does not
affect the original string substring and original string can share same underlying memory, cheap to make
substrings and copies
"..." string literal
`...` raw string literal, no need to escape characters

### UTF-8
high order bit of the first byte of rune indicate how many bytes follow
can be decoded from left to right withou looking ahead, we can test prefix suffix easily
\uxxxx for 16-bit values
\Uhhhhhhhh for 32-bit values
\u4e16\u754c
go's range loop, when applied to string, will do rune decoding implicitly

### important packages: bytes strings strconv unicode
use bytes.Buffer to build a string incrementally
path and path/filepath modules provides functionalities to handle directory paths
value of a byte slice []byte can be freely modified
bytes.Buffer: WriteString(), WriteByte(), WriteRune(), String() methods to build a string
strconv: conversion between string and other types: Itoa Atoi ParseInt

## Constants
const (
  e = 3.2
  f = 3.3
)
constants can appear in types, especially array length(known at compile time)

### constant generator iota
begins at 0, increments for each item in the sequence

    type Weekday int
    const (
      Sunday Weekday = iota // 0
      Monday // 1
      Tuesday // 2
    )

iota can be used in more complex examples: 1 << iota, the next will be 1 << 1, then 1 << 2, ...

### untyped constants
more precisions : 256+ bits of precision

# Ch4 Composite Types

## Arrays

    var q [3]int = [3]int{1, 2, 3}
    r := [...]int{3, 3, 4, 5} // elipsis ... : length decided by elements in the init-list

fixed size, rarely used directly in Go, slices are more flexible
len() return the num elements of an array
range arr : return index and value tuple: i, v := range arr
size of array is part of the type, so [3]int and [4]int are different types
indices can appear in any order:

    type Currency int
    const (
      USD Currency = iota
      EUR
      GBP
      RMB
    )
    symbols := [...]string{EUR: "$", USD: "#", RMB:"￥"}
    // others are default value (zero value)

if elements are comparable, the arrays are comparable
pass arrays will lead to a copy, which is inefficient.
We can pass a pointer of array to a function: ptr *[32]byte, but this is not flexible:
type is fixed, and we cannot add/remove elements fromt the array, in Go, we use slices

## slices
[]T is called slice of T, it gives access to its underlying array(part of)
a slice has three parts: 

1. a pointer: point to the element in the underlying array
2. length len(), cannot exceed end of underlying array
3. capacity cap()

s[i:j] creates a slice
slicing beyond cap() causes panic, but slice beyond len() extends the slice
slice contains pointer to the underlying array, so copying slices creates aliases for the underlying array
initialize a slice:

    s := []int{1, 2, 3}  // different with arrays, length not given
    // This implicitly creates an array variable and a slice to refer to it
  
Unlike arrays, slices are not comparable, it can only be compared with nil
test if a slice is empty, use len(s) == 0, not s == nil

make function to create slices:

    make([]T, len)
    make([]T, len, cap)
  
### append() function
the builtin append() function append elements to an array, under the hood, it will try to expand
if capacity is not enough, create a new slice(new array) and copy if necessary. we don't know if append will cause
reallocation, so s = append(s, 1) is the idiom for Go.

    var x []int
    x = append(x, 1)
    x = append(x, 2, 3)
    x = append(x, x...) // variadic, append a slice

