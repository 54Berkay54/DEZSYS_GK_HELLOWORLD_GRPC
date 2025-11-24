import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class DWRecordServiceImpl extends DataWarehouseServiceGrpc.DataWarehouseServiceImplBase {

    private final List<DWRecord.WarehouseData> storage = new ArrayList<>();

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