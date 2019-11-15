# Avro

-   Avro is an open source language neutral data serialization format.
-   Avro Data is described in a langauge independent schema.
    -   The schema is described in JSON and the serialization is usually in to binary files.
        -   The Avro Schema file resides in a schema registry.
    -   Avro assumes the schema is present while reading and writing the files.
    
##  Advantages of Avro

-   Whenever a data change from one format to another there is a always a struggle to make sure all the consuming teams are ready to process the new event.
-   With **Avro** the schema change from one format to another makes this process easier.
    -   Read the example in the book, it is very well explained.       

## Avro Files

-   **Avro** File is a concept where the entire schema and the data is together. 
