package fitloop.upload.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import fitloop.member.dto.request.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("access") String accessToken
    ) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 파일 이름 설정
                String fileName = "uploads/" + userDetails.getUsername() + "/" + file.getOriginalFilename();

                // 메타데이터 설정
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                // S3 업로드
                amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);

                // 업로드된 파일 URL 생성
                String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
                fileUrls.add(fileUrl);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
            }
        }

        // 모든 파일 업로드 후 URL 목록 반환
        return ResponseEntity.ok(fileUrls);
    }
}
