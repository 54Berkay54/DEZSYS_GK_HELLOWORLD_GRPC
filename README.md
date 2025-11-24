# Middleware Engineering "DEZSYS_GK_HELLOWORLD_GRPC"

Verfasser: **Berkay Genc**

## Aufgabenstellung

**GK – Grundlagen**

Entwickeln Sie ein einfaches gRPC-System mit HelloWorld-Service.  
Erstellen Sie dazu eine Proto-Datei, implementieren Sie einen gRPC-Server und einen gRPC-Client und dokumentieren Sie alle Schritte sowie die Programmausgabe.

**EK – Erweiterte Grundlagen**

Erweitern Sie den Dienst so, dass ein DataWarehouse-Eintrag übertragen werden kann.
Dokumentieren Sie, welche Teile des Programms dafür angepasst werden mussten.

**Vertiefung**

Implementieren Sie einen zweiten DataWarehouse-Client in einer anderen Programmiersprache und dokumentieren Sie die Umsetzung.

## Questions

- **What is gRPC and why does it work accross languages and platforms?**

gRPC is a high-performance remote procedure call framework that works across languages and platforms because it uses HTTP/2 and the language-neutral Protocol Buffers interface definition system.

- **Describe the RPC life cycle starting with the RPC client?**

The RPC lifecycle starts when the client calls a stub method, which serializes the request into protobuf, sends it over HTTP/2 to the server, where the service implementation processes it and returns a serialized response that the client then deserializes.

- **Describe the workflow of Protocol Buffers?**

The workflow of Protocol Buffers consists of defining messages in a `.proto` file, compiling them with protoc to generate language-specific classes, and using those classes to serialize and deserialize structured data.

- **What are the benefits of using protocol buffers?**

The benefits of protocol buffers include compact binary encoding, fast serialization, strong typing, compatibility across versions, and automatic code generation for many languages.

- **When is the use of protocol not recommended?**

Protocol buffers are not recommended when human-readable text formats are required or when highly dynamic, self-describing data structures are needed without recompiling schemas.

- **List 3 different data types that can be used with protocol buffers?**

Three data types that can be used with protocol buffers are int32, string, and bool.

## Implementierung

##### GK

Start HelloWorldServer (Java)  
`gradle clean build`  
`gradle runServer`

Start HelloWorldClient (Java)  
`gradle runClient`

-------------------------------- Python 

Create a Python virtual environment in the project folder

`python -m venv venv`

Activate the virtual environmet

`.\venv\Scripts\activate`

Add grpcio packages  
`pip3 install grpcio grpcio-tools`  

Compile .proto file  
`python3 -m grpc_tools.protoc -I src/main/proto  
  --python_out=src/main/resources  
  --grpc_python_out=src/main/resources  
  src/main/proto/hello.proto`  

Start HelloWorldClient (Python)  
`python3 src/main/resources/helloWorldClient.py`  

##### EK

`build.gradle`

```groovy
tasks.register('runServer', JavaExec) {
    group = 'application'
    description = 'Runs the gRPC server'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'HelloWorldServer'
}

tasks.register('runServerDWR', JavaExec) {
    group = 'application'
    description = 'Runs the gRPC server'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'DWRecordServer'
}

tasks.register('runClient', JavaExec) {
    group = 'application'
    description = 'Runs the gRPC client'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'HelloWorldClient'
    args 'Max'
    args 'Mustermann'
}
```

**Defining the DWRecord.proto file**

```java
syntax = "proto3";

service DataWarehouseService {
  rpc InsertWarehouse (WarehouseData) returns (InsertResponse);
  rpc GetWarehouseById (WarehouseRequest) returns (WarehouseData);
}

message WarehouseData {
  string warehouseID = 1;
  string warehouseName = 2;
  string timestamp = 3;
  string warehouseAddress = 4;
  string warehousePostalCode = 5;
  string warehouseCity = 6;
  string warehouseCountry = 7;

  repeated WarehouseProduct products = 8;
}

message WarehouseProduct {
  string productID = 1;
  string productName = 2;
  string productCategory = 3;
  int32 productQuantity = 4;
  string productUnit = 5;
}

message InsertResponse {
  bool success = 1;
  string message = 2;
}

message WarehouseRequest {
  string warehouseID = 1;
}
```

**Implementing the Server**

The server loads the generated stubs, implements the RPC and starts a gRPC server on port 50051.

```java
@Override
    public void insertWarehouse(DWRecord.WarehouseData request, StreamObserver<DWRecord.InsertResponse> responseObserver) {

        System.out.println("Received WarehouseData: " + request.getWarehouseID());

        storage.add(request);

        DWRecord.InsertResponse response = DWRecord.InsertResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Warehouse stored: " + request.getWarehouseID())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
```

**Implementing the Client**

A Client to test the server.

```java
ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        DataWarehouseServiceGrpc.DataWarehouseServiceBlockingStub stub =
                DataWarehouseServiceGrpc.newBlockingStub(channel);

        DWRecord.WarehouseProduct product = DWRecord.WarehouseProduct.newBuilder()
                .setProductID("P001")
                .setProductName("CPU")
                .setProductCategory("Electronics")
                .setProductQuantity(20)
                .setProductUnit("pcs")
                .build();

        DWRecord.WarehouseData warehouse = DWRecord.WarehouseData.newBuilder()
                .setWarehouseID("W001")
                .setWarehouseName("Warehouse 1")
                .setTimestamp("18-11-2025")
                .setWarehouseAddress("Wexstraße 19-23")
                .setWarehousePostalCode("1220")
                .setWarehouseCity("Wien")
                .setWarehouseCountry("Austria")
                .addProducts(product)
                .build();

        DWRecord.InsertResponse response = stub.insertWarehouse(warehouse);
        System.out.println("InsertResponse: " + response.getMessage());

        DWRecord.WarehouseData fetched = stub.getWarehouseById(
                DWRecord.WarehouseRequest.newBuilder()
                        .setWarehouseID("W001")
                        .build()
        );

        System.out.println("Fetched warehouse name: " + fetched.getWarehouseName());

        channel.shutdown();
```

## Quellen

- [https://grpc.io/](https://grpc.io/)
- [Quick start | Java | gRPC](https://grpc.io/docs/languages/java/quickstart/)
- [gRPC-Spring-Boot-Starter Documentation | grpc-spring-boot-starter](https://yidongnan.github.io/grpc-spring-boot-starter/en/)
- https://blog.shiftasia.com/introduction-grpc-and-implement-with-spring-boot/
- [Introduction to gRPC | Baeldung](https://www.baeldung.com/grpc-introduction)
- https://intuting.medium.com/implement-grpc-service-using-java-gradle-7a54258b60b8
- Find my HelloWorld source files here: [Hier können Sie sich anmelden | TGM](https://elearning.tgm.ac.at/mod/resource/view.php?id=188440)
- Intro Video (5min): [Introduction to gRPC | gRPC](https://grpc.io/docs/what-is-grpc/introduction)
- Intro Video (15min): [What is gRPC? | gRPC](https://grpc.io/docs/what-is-grpc)
- Youtube Video: https://www.youtube.com/watch?v=QX2AAY_hAQI
