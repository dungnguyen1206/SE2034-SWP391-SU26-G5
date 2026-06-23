package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
     String uploadImage(MultipartFile file);

}
