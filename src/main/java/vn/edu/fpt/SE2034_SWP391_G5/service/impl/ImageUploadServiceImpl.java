package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) {

        try {
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap());
            
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }



    }

    @Override
    public void deleteImage(String imageUrl) {
        // Chỉ xóa nếu là ảnh từ Cloudinary
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return;
        }
        try {
            // 1. Lấy tên file sau dấu gạch chéo cuối cùng (ví dụ: "xyz123.jpg")
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            // 2. Bỏ phần mở rộng (ví dụ: ".jpg")
            String publicId = filename.substring(0, filename.lastIndexOf("."));

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Xóa ảnh thất bại: " + e.getMessage());
        }
    }
}
