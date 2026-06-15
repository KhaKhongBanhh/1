package grpc;


import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class sum_of_integer {

    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2240;
        String studentCode = "B22DCDT095";
        String questionAlias = "o6j02HQv";

        System.out.println("Đang kết nối tới gRPC Server...");
        // 1. Tạo kênh kết nối (không dùng TLS / plaintext)
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            // Khởi tạo Stub (bản dùng để gọi đồng bộ)
            JudgeServiceGrpc.JudgeServiceBlockingStub stub = JudgeServiceGrpc.newBlockingStub(channel);

            // ==========================================
            // BƯỚC 1: GỌI HÀM REQUEST ĐỂ LẤY DỮ LIỆU
            // ==========================================
            JudgeRequest request = JudgeRequest.newBuilder()
                    .setStudentCode(studentCode)
                    .setQuestionAlias(questionAlias)
                    .build();

            JudgeResponse response = stub.request(request);
            String requestId = response.getRequestId();
            String data = response.getData();
            
            System.out.println("Nhận requestId: " + requestId);
            System.out.println("Nhận data: " + data);

            // ==========================================
            // BƯỚC 2: XỬ LÝ DỮ LIỆU (Tách chuỗi & tính tổng)
            // ==========================================
            long sum = 0;
            if (data != null && !data.isEmpty()) {
                String[] numbers = data.split(",");
                for (String numStr : numbers) {
                    sum += Long.parseLong(numStr.trim());
                }
            }
            System.out.println("Tổng tính được: " + sum);

            // ==========================================
            // BƯỚC 3: GỌI HÀM SUBMIT TRẢ KẾT QUẢ
            // ==========================================
            SubmitRequest submitRequest = SubmitRequest.newBuilder()
                    .setStudentCode(studentCode)
                    .setQuestionAlias(questionAlias)
                    .setRequestId(requestId)
                    .setAnswer(String.valueOf(sum))
                    .build();

            SubmitResponse submitResponse = stub.submit(submitRequest);
            
            System.out.println("Kết quả chấm điểm: " + submitResponse.getStatus());
            System.out.println("Lời nhắn từ server: " + submitResponse.getMessage());

        } catch (Exception e) {
            System.err.println("Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng kênh kết nối sau khi hoàn thành
            if (channel != null && !channel.isShutdown()) {
                channel.shutdown();
                System.out.println("Đã đóng kết nối gRPC.");
            }
        }
    }
}