import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DWRecordClient {

    public static void main(String[] args) {

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

        System.out.println("warehouse id: " + fetched.getWarehouseID());
        System.out.println("warehouse name: " + fetched.getWarehouseName());
        System.out.println("warehouse address: " + fetched.getWarehouseAddress());
        System.out.println("timestamp: " + fetched.getTimestamp());
        System.out.println("warehouse address: " + fetched.getWarehouseAddress());
        System.out.println("warehouse postal code: " + fetched.getWarehousePostalCode());
        System.out.println("warehouse city: " + fetched.getWarehouseCity());
        System.out.println("warehouse country: " + fetched.getWarehouseCountry());
        System.out.println("warehouse products: " + fetched.getProductsList());


        channel.shutdown();
    }
}