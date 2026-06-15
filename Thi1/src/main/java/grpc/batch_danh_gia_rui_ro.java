package grpc;


import java.util.ArrayList;
import java.util.List;

import GRPC.TransactionRecord;
import GRPC.TransactionRiskAnswer;
import GRPC.TransactionRiskBatchData;
import GRPC.TypedJudgeRequest;
import GRPC.TypedJudgeResponse;
import GRPC.TypedJudgeServiceGrpc;
import GRPC.TypedSubmitRequest;
import GRPC.TypedSubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class batch_danh_gia_rui_ro {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2240;

        String studentCode = "B22DCDT095";
        String questionAlias = "ajJtuKdx";

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        try {

            TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub stub =
                    TypedJudgeServiceGrpc.newBlockingStub(channel);

            // =========================
            // REQUEST
            // =========================
            TypedJudgeRequest request =
                    TypedJudgeRequest.newBuilder()
                            .setStudentCode(studentCode)
                            .setQuestionAlias(questionAlias)
                            .build();

            TypedJudgeResponse response =
                    stub.requestTyped(request);

            String requestId = response.getRequestId();

            TransactionRiskBatchData data =
                    response.getTransactionRiskBatch();

            List<String> highRiskIds = new ArrayList<>();

            double totalHighRiskAmount = 0.0;

            for (TransactionRecord tx : data.getTransactionsList()) {

                boolean highRisk =
                        tx.getAmount() >= 5000
                        || tx.getChargebackCount() >= 2
                        || (tx.getNewDevice()
                            && !tx.getCountry().equals("VN"));

                if (highRisk) {
                    highRiskIds.add(tx.getTransactionId());
                    totalHighRiskAmount += tx.getAmount();
                }
            }

            totalHighRiskAmount =
                    Math.round(totalHighRiskAmount * 100.0) / 100.0;

            int reviewCount = highRiskIds.size();

            System.out.println("High Risk IDs:");
            for (String id : highRiskIds) {
                System.out.println(id);
            }

            System.out.println("Review Count = " + reviewCount);
            System.out.println("Total Amount = " + totalHighRiskAmount);

            // =========================
            // ANSWER
            // =========================
            TransactionRiskAnswer answer =
                    TransactionRiskAnswer.newBuilder()
                            .addAllHighRiskTransactionIds(highRiskIds)
                            .setReviewCount(reviewCount)
                            .setTotalHighRiskAmount(totalHighRiskAmount)
                            .build();

            TypedSubmitRequest submitRequest =
                    TypedSubmitRequest.newBuilder()
                            .setStudentCode(studentCode)
                            .setQuestionAlias(questionAlias)
                            .setRequestId(requestId)
                            .setTransactionRiskAnswer(answer)
                            .build();

            TypedSubmitResponse submitResponse =
                    stub.submitTyped(submitRequest);

            System.out.println("\nStatus: "
                    + submitResponse.getStatus());

            System.out.println("Message: "
                    + submitResponse.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }
}