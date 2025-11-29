import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class DWRecordServiceImpl extends DataWarehouseServiceGrpc.DataWarehouseServiceImplBase {

    private final List<DWRecord.WarehouseData> storage = new ArrayList<>();

    @Override
    public void insertWarehouse(DWRecord.WarehouseData request, StreamObserver<DWRecord.InsertResponse> responseObserver) {

        System.out.println("Received WarehouseData: ");
        System.out.println("Warehouse ID: " + request.getWarehouseID());
        System.out.println("Warehouse name: " + request.getWarehouseName());
        System.out.println("Timestamp: " + request.getTimestamp());
        System.out.println("Warehouse address: " + request.getWarehouseAddress());
        System.out.println("Warehouse postal code: " + request.getWarehousePostalCode());
        System.out.println("Warehouse city: " + request.getWarehouseCity());
        System.out.println("Warehouse country: " + request.getWarehouseCountry());
        System.out.println("Received Warehouse products: " + request.getProductsList());

        storage.add(request);

        DWRecord.InsertResponse response = DWRecord.InsertResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Warehouse stored: " + request.getWarehouseID())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getWarehouseById(DWRecord.WarehouseRequest request, StreamObserver<DWRecord.WarehouseData> responseObserver) {

        for (DWRecord.WarehouseData data : storage) {
            if (data.getWarehouseID().equals(request.getWarehouseID())) {
                responseObserver.onNext(data);
                responseObserver.onCompleted();
                return;
            }
        }

        responseObserver.onCompleted();
    }
}