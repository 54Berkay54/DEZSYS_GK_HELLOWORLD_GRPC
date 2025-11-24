import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DWRecordServer {

    public static void main(String[] args) throws Exception {

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new DWRecordServiceImpl())
                .build();

        server.start();
        System.out.println("gRPC Server running on port 50051");
        server.awaitTermination();
    }
}