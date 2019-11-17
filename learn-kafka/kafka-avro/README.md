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

-   **Avro** File is a concept where the entire schema and the data is present as part of the file.
    -   But this approach has issues because when you expect the entire schema to be present as part of the record usually results in double the size of the record.

## Schema Registry

 -   **Schema Registry** is one of the established pattern where you can store the **Schema**
-   This is not part of **Kafka**, but the Confluent platform comes with it.
-   The idea is to store all the schema that are used to write data to kafka in the registry.
-   Then we simply provide the identifier of the schema as part of the record as we produce the data in to Kafka.
    -   The Consumers then can use the identifier to read the schema from the registry and de-serialize the data.          
           

