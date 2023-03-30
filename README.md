
# AURORAL Knowledge Graph Builder

The Knowledge Graph Builder (KG Builder) is an *horizontal service* of the [AURORAL platform](https://www.auroral.eu/#/) developed by **Universidad Politécnica de Madrid**.

This *horizontal service* provides **views for data located in a local or a remote AURORAL node**, **thing descriptions (meta-data) discoverable from an AURORAL node**, and **data from third-party services**. In addition, this provides views feed with  **data aggregation from the aforementioned sources**.


## Quickstart

There are two methods to deploy the Knowledge Graph Builder. Simplest method is to run the following Docker recipe. 

````yml
version: '3'
services:
  helio-publisher:
    image: acimmino/kgbuilder:4.0.0
    volumes: 
      - ./views/:/usr/src/kgb/views/
      - ./db/:/usr/src/kgb/db/
    ports:
      - "4567:4567"
````
As a result two folders are created
 * The folder `views` contains the HTML templates used by the service. Initially is empty, a [default view could be downloaded]() and pasted in this folder for a quickstart.
 * The folder `db`  contains the storage of this service, it is recommended to make periodical copies of the content as backups.

Alternatively, download the [latest release](https://github.com/AuroralH2020/kg-builder/releases) and follow the instructions. 




