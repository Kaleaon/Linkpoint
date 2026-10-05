# serde-llsd

Serialization library for Linden Lab Serial Data format, and xlm-rpc. Rust/Serde version.

Linden Lab Structured Data (LLSD) serialization

This is a serialization system used by Second Life and Open Simulator.
It is documented here: http://wiki.secondlife.com/wiki/LLSD

## Introduction

There are three formats - XML, binary, and "Notation". All store
the same data, which is roughly the same as what JSON can represent.
Parsing and output functions are provided.

## Status

XML, binary, and Notation versions are implemented.

Unit tests pass. Tested against Second Life asset servers and Open Simulator servers.
Used by the Sharpview metaverse viewer.

## Data types

- Boolean - converts to Rust "bool".
- Integer - Rust i32.
- Real - Rust f64
- UUID - Rust [u8;16]
- String - Rust String, Unicode
- Date - "an absolute point in time, UTC, with resolution to the second", as Rust i64.
- URI - Rust String that is a URI
- Binary - Vec<u8>

- A map is a HashMap mapping String keys to LLSD values.

- An array is a Rust Vec of LLSD values.

## Field access

The **enum_as_inner** crate is used to derive access functions for each field type.
So, given an LLSDValue llsdval which is expected to be an Integer,

    let n = *llsdval.as_integer().unwrap();

will yield the integer value.

## LLSD values in Rust

These generally follow the conventions of the Rust crate "json".
An LLSD value is a tree.

## Character sets

Notation is divided into a byte stream form and a string from.

The byte stream form supports all the formats defined for LLSD, including
byte-counted strings and binary values. Only single-byte ASCII characters are allowed.
Byte stream form Notation cannot be placed inside LLSD XML, because the byte streams
will not be valid UTf-8.

The string form is valid UTF-8, but will not accept byte-counted strings or binary values.
Binary values must be in hex or Base64 format. String-form Notation can be placed inside
LLSD XML.

## Known problems.

- "Notation" fomat input will not currently accept infinity or NaN values.

- Error messages do not indicate the source of the problem in the incoming stream.
